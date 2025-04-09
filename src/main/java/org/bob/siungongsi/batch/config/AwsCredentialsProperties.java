package org.bob.siungongsi.batch.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;

@Profile("batch")
@ConfigurationProperties(prefix = "spring.cloud.aws.credentials")
public record AwsCredentialsProperties(String accessKey, String secretKey) {}
