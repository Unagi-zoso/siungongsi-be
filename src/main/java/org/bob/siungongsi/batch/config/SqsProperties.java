package org.bob.siungongsi.batch.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;

@Profile("batch")
@ConfigurationProperties(prefix = "spring.cloud.aws.sqs.queue")
public record SqsProperties(
    String name,
    String url,
    String region,
    int maxNumberOfMessages,
    int waitTimeSeconds,
    int delaySeconds) {}
