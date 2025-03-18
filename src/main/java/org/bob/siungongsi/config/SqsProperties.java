package org.bob.siungongsi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.cloud.aws.sqs.queue")
public record SqsProperties(
    String name,
    String url,
    String region,
    int maxNumberOfMessages,
    int waitTimeSeconds,
    int visibilityTimeout) {}
