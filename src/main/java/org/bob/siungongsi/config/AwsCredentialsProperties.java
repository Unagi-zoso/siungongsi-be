package org.bob.siungongsi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.cloud.aws.credentials")
public record AwsCredentialsProperties(String accessKey, String secretKey) {}
