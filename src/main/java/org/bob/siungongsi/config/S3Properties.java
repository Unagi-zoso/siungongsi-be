package org.bob.siungongsi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;

@Profile("batch")
@ConfigurationProperties(prefix = "spring.cloud.aws.s3")
public record S3Properties(
    String bucket, String region, String accessKeyId, String secretAccessKey) {}
