package org.bob.siungongsi.batch.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

@Profile("batch")
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
