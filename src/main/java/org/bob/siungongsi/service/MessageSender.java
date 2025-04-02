package org.bob.siungongsi.service;

import java.util.concurrent.CompletableFuture;

import org.bob.siungongsi.config.SqsProperties;
import org.bob.siungongsi.event.GongsiMessage;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.sentry.Sentry;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

@Profile("batch")
@Service
public class MessageSender {

  private final SqsAsyncClient sqsAsyncClient;
  private final String queueUrl;
  private final int delaySeconds;
  private final ObjectMapper objectMapper;

  public MessageSender(
      SqsAsyncClient sqsAsyncClient, SqsProperties sqsProperties, ObjectMapper objectMapper) {
    this.sqsAsyncClient = sqsAsyncClient;
    this.queueUrl = sqsProperties.url();
    this.delaySeconds = sqsProperties.delaySeconds();
    this.objectMapper = objectMapper;
  }

  public CompletableFuture<SendMessageResponse> sendGongsiMessage(GongsiMessage message) {
    try {
      String messageBody = objectMapper.writeValueAsString(message);

      SendMessageRequest request =
          SendMessageRequest.builder()
              .queueUrl(queueUrl)
              .messageBody(messageBody)
              .delaySeconds(delaySeconds)
              .build();

      return sqsAsyncClient
          .sendMessage(request)
          .exceptionally(
              ex -> {
                Sentry.captureException(ex);
                ex.printStackTrace();
                return null;
              });

    } catch (JsonProcessingException e) {
      Sentry.captureException(e);
      throw new RuntimeException("Failed to serialize message", e);
    }
  }
}
