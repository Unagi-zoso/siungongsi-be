package org.bob.siungongsi.api.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("!batch")
@Configuration
@EnableConfigurationProperties(CorsProperties.class)
public class ApiPropertiesConfig {}
