package org.bob.siungongsi.service;

import static org.bob.siungongsi.util.GongsiDataProcessingTimeChecker.isNotWithinProcessingTime;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.bob.siungongsi.client.OpenDartReader;
import org.bob.siungongsi.config.SqsProperties;
import org.bob.siungongsi.domain.CompanyEntity;
import org.bob.siungongsi.domain.GongsiEntity;
import org.bob.siungongsi.domain.ProcessingFailedGongsiEntity;
import org.bob.siungongsi.event.GongsiMessage;
import org.bob.siungongsi.repository.CompanyRepository;
import org.bob.siungongsi.repository.GongsiRepository;
import org.bob.siungongsi.repository.ProcessingFailedGongsiRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.model.S3Object;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.sentry.Sentry;
import jakarta.annotation.PreDestroy;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

@Service
public class MessageListener {

  private final SqsAsyncClient sqsAsyncClient;
  private final String queueUrl;
  private final int maxNumberOfMessages;
  private final int waitTimeSeconds;
  private final ObjectMapper objectMapper;
  private final OpenDartReader openDartReader;
  private final GongsiSummarizer gongsiSummarizer;
  private final FileService fileService;
  private final ZipFileReader zipFileReader;
  private final GongsiRepository gongsiRepository;
  private final CompanyRepository companyRepository;
  private final String originalUrl;
  private final ProcessingFailedGongsiRepository processingFailedGongsiRepository;

  public MessageListener(
      SqsAsyncClient sqsAsyncClient,
      SqsProperties sqsProperties,
      ObjectMapper objectMapper,
      OpenDartReader openDartReader,
      GongsiSummarizer gongsiSummarizer,
      FileService fileService,
      ZipFileReader zipFileReader,
      GongsiRepository gongsiRepository,
      CompanyRepository companyRepository,
      @Value("${opendart.viewer.url}") String originalUrl,
      ProcessingFailedGongsiRepository processingFailedGongsiRepository) {
    this.sqsAsyncClient = sqsAsyncClient;
    this.queueUrl = sqsProperties.url();
    this.maxNumberOfMessages = sqsProperties.maxNumberOfMessages();
    this.waitTimeSeconds = sqsProperties.waitTimeSeconds();
    this.objectMapper = objectMapper;
    this.openDartReader = openDartReader;
    this.gongsiSummarizer = gongsiSummarizer;
    this.fileService = fileService;
    this.zipFileReader = zipFileReader;
    this.gongsiRepository = gongsiRepository;
    this.companyRepository = companyRepository;
    this.originalUrl = originalUrl;
    this.processingFailedGongsiRepository = processingFailedGongsiRepository;
  }

  @Scheduled(fixedRate = 30000) // 30초 (30,000ms)
  public void processMessages() {
    if (isNotWithinProcessingTime()) {
      return;
    }

    try {
      ReceiveMessageRequest receiveRequest =
          ReceiveMessageRequest.builder()
              .queueUrl(queueUrl)
              .maxNumberOfMessages(maxNumberOfMessages)
              .waitTimeSeconds(waitTimeSeconds)
              .build();

      List<Message> messages = sqsAsyncClient.receiveMessage(receiveRequest).get().messages();

      for (Message message : messages) {
        if (handleMessage(message)) {
          deleteMessage(message);
        }
      }

    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
    }
  }

  private boolean handleMessage(Message message) {
    try {
      GongsiMessage gongsiMessage = objectMapper.readValue(message.body(), GongsiMessage.class);

      processGongsi(gongsiMessage);

      return true;

    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  private void deleteMessage(Message message) {
    DeleteMessageRequest deleteRequest =
        DeleteMessageRequest.builder()
            .queueUrl(queueUrl)
            .receiptHandle(message.receiptHandle())
            .build();
    sqsAsyncClient.deleteMessage(deleteRequest);
  }

  public void processGongsi(GongsiMessage message) {

    try {
      String gongsiId = message.receiptNo();
      String s3Key = fileService.generateS3Key(gongsiId + ".zip");

      if (fileService.doesFileNotExist(s3Key)) {
        byte[] file = openDartReader.fetchGongsiDocument(gongsiId);

        if (!fileService.isZipFile(file)) {
          ProcessingFailedGongsiEntity processingFailedGongsi =
              new ProcessingFailedGongsiEntity(message.receiptNo());
          processingFailedGongsiRepository.save(processingFailedGongsi);
          return;
        }
        fileService.uploadFile(s3Key, new ByteArrayInputStream(file), file.length);
      }
      uploadGongsi(message);
    } catch (Exception e) {
      Sentry.captureException(e);
      e.printStackTrace();
    }
  }

  public void uploadGongsi(GongsiMessage message) {
    String s3Key = fileService.generateS3Key(message.receiptNo() + ".zip");
    S3Object s3Object = fileService.downloadFile(s3Key);
    try {
      String text =
          new String(
              zipFileReader.readZipFile(s3Object.getObjectContent().readAllBytes()),
              StandardCharsets.UTF_8);
      String summarized = gongsiSummarizer.summarizeText(text);
      CompanyEntity company = companyRepository.findByCompanyCode(message.companyCode());
      gongsiRepository.save(
          new GongsiEntity(
              company,
              summarized,
              message.receiptNo(),
              message.receiptTitle(),
              originalUrl + message.receiptNo(),
              s3Key));
    } catch (IOException e) {
      Sentry.captureException(e);
      throw new RuntimeException(e);
    }
  }

  @PreDestroy
  public void shutdownSqsClient() {
    sqsAsyncClient.close();
  }
}

@Component
class SqsShutdownListener implements ApplicationListener<ContextClosedEvent> {

  private final SqsAsyncClient sqsAsyncClient;

  public SqsShutdownListener(SqsAsyncClient sqsAsyncClient) {
    this.sqsAsyncClient = sqsAsyncClient;
  }

  @Override
  public void onApplicationEvent(ContextClosedEvent event) {
    sqsAsyncClient.close();
  }
}
