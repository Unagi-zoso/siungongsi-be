package org.bob.siungongsi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.cloud.aws.s3")
public record S3Properties(
    String bucket, String region, String accessKeyId, String secretAccessKey) {}
