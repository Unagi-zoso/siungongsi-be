package org.bob.siungongsi.batch.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("batch")
@EnableConfigurationProperties({
  AwsCredentialsProperties.class,
  S3Properties.class,
  SqsProperties.class
})
public class BatchOnlyPropertiesConfig {}
