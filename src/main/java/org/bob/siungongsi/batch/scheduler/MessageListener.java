package org.bob.siungongsi.batch.scheduler;

import static org.bob.siungongsi.batch.util.GongsiDataProcessingTimeChecker.isNotWithinProcessingTime;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.bob.siungongsi.batch.config.SqsProperties;
import org.bob.siungongsi.batch.event.GongsiMessage;
import org.bob.siungongsi.batch.service.FileService;
import org.bob.siungongsi.batch.service.GongsiSummarizer;
import org.bob.siungongsi.batch.service.OpenDartReader;
import org.bob.siungongsi.batch.service.ZipFileReader;
import org.bob.siungongsi.common.domain.CompanyEntity;
import org.bob.siungongsi.common.domain.GongsiEntity;
import org.bob.siungongsi.common.domain.GongsiSentStatusEntity;
import org.bob.siungongsi.common.domain.ProcessingFailedGongsiEntity;
import org.bob.siungongsi.common.repository.CompanyRepository;
import org.bob.siungongsi.common.repository.GongsiRepository;
import org.bob.siungongsi.common.repository.GongsiSentStatusRepository;
import org.bob.siungongsi.common.repository.ProcessingFailedGongsiRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
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
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

@Profile("batch")
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
  private final GongsiSentStatusRepository gongsiSentStatusRepository;

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
      ProcessingFailedGongsiRepository processingFailedGongsiRepository,
      GongsiSentStatusRepository gongsiSentStatusRepository) {
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
    this.gongsiSentStatusRepository = gongsiSentStatusRepository;
  }

  @Scheduled(fixedRate = 30000) // 30초 (30,000ms)
  public void processMessages() {
    if (isNotWithinProcessingTime()) {
      return;
    }

    ReceiveMessageRequest receiveRequest =
        ReceiveMessageRequest.builder()
            .queueUrl(queueUrl)
            .maxNumberOfMessages(maxNumberOfMessages)
            .waitTimeSeconds(waitTimeSeconds)
            .build();

    sqsAsyncClient
        .receiveMessage(receiveRequest)
        .thenApply(ReceiveMessageResponse::messages)
        .thenAccept(
            messages -> {
              for (Message message : messages) {
                if (handleMessage(message)) {
                  deleteMessage(message);
                }
              }
            })
        .exceptionally(
            e -> {
              e.printStackTrace();
              return null;
            });
  }

  private boolean handleMessage(Message message) {
    try {
      GongsiMessage gongsiMessage = objectMapper.readValue(message.body(), GongsiMessage.class);
      return processGongsi(gongsiMessage);
    } catch (Exception e) {
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

  public boolean processGongsi(GongsiMessage message) {

    try {
      String gongsiId = message.receiptNo();
      String s3Key = fileService.generateS3Key(gongsiId + ".zip");

      if (fileService.doesFileNotExist(s3Key)) {
        byte[] file = openDartReader.fetchGongsiDocument(gongsiId);

        if (!fileService.isZipFile(file)) {
          ProcessingFailedGongsiEntity processingFailedGongsi =
              new ProcessingFailedGongsiEntity(
                  message.receiptNo(), message.companyCode(), message.receiptTitle());
          processingFailedGongsiRepository.save(processingFailedGongsi);
          return false;
        }
        fileService.uploadFile(s3Key, new ByteArrayInputStream(file), file.length);
      }
      uploadGongsi(message);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
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
      GongsiEntity gongsiEntity =
          gongsiRepository.save(
              new GongsiEntity(
                  company,
                  summarized,
                  message.receiptNo(),
                  message.receiptTitle(),
                  originalUrl + message.receiptNo(),
                  s3Key));
      // 내부 호출은 어노테이션이 적용 안되는 것으로 아는데
      // 그렇다고 최상단 메소드에 트랜잭션을 적용하자니 커넥션을 너무 오래 잡을 것 같아
      // 트랜잭션 없이 처리함. 불일치가 많이 발생한다면 추후 수정 필요
      gongsiSentStatusRepository.save(new GongsiSentStatusEntity(gongsiEntity));
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
@Profile("batch")
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
