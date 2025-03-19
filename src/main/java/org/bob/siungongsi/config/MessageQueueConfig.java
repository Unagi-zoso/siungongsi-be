package org.bob.siungongsi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

@Configuration
public class MessageQueueConfig {

  @Bean
  public SqsAsyncClient sqsAsyncClient(
      SqsProperties sqsProperties, AwsCredentialsProperties awsCredentialsProperties) {
    return SqsAsyncClient.builder()
        .region(Region.of(sqsProperties.region()))
        .credentialsProvider(
            StaticCredentialsProvider.create(
                AwsBasicCredentials.create(
                    awsCredentialsProperties.accessKey(), awsCredentialsProperties.secretKey())))
        .build();
  }
}
