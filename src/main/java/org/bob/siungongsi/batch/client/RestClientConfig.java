package org.bob.siungongsi.batch.client;

import static org.bob.siungongsi.common.client.ClientFactory.createClient;

import java.util.Map;

import org.bob.siungongsi.batch.client.clientinterface.GoogleAiClientInterface;
import org.bob.siungongsi.batch.client.clientinterface.OpenDartClientInterface;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("batch")
@Configuration
public class RestClientConfig {
  private static final String OPEN_DART_URL = "https://opendart.fss.or.kr/api";
  private static final String GOOGLE_AI_STUDIO_URL =
      "https://generativelanguage.googleapis.com/v1beta";

  @Bean
  public OpenDartClientInterface openDartClient(@Value("${opendart.api.key}") String apiKey) {
    return createClient(
        OPEN_DART_URL, OpenDartClientInterface.class, null, Map.of("crtfc_key", apiKey));
  }

  @Bean
  public GoogleAiClientInterface googleAiClient(@Value("${google-ai.api.key}") String apiKey) {
    return createClient(
        GOOGLE_AI_STUDIO_URL, GoogleAiClientInterface.class, null, Map.of("key", apiKey));
  }
}
