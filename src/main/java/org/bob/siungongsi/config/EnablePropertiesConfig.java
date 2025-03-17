package org.bob.siungongsi.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties({
  AwsCredentialsProperties.class,
  S3Properties.class,
  SqsProperties.class
})
@Configuration
public class EnablePropertiesConfig {}
