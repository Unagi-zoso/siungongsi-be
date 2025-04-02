package org.bob.siungongsi.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;

@Profile("!batch")
@ConfigurationProperties(prefix = "cors")
public record CorsProperties(List<String> allowedOrigins) {}
