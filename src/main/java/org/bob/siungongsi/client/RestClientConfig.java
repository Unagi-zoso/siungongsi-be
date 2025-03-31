package org.bob.siungongsi.client;

import java.net.http.HttpClient;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.bob.siungongsi.client.clientinterface.GoogleAiClientInterface;
import org.bob.siungongsi.client.clientinterface.OpenDartClientInterface;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Profile("batch")
@Configuration
public class RestClientConfig {
  private static final String OPEN_DART_URL = "https://opendart.fss.or.kr/api";
  private static final String GOOGLE_AI_STUDIO_URL =
      "https://generativelanguage.googleapis.com/v1beta";

  private boolean virtualThreadEnabled = true;

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

  private <T> T createClient(
      String baseUrl,
      Class<T> clientClass,
      Map<String, String> headers,
      Map<String, String> params) {

    if (params != null && !params.isEmpty()) {
      String queryParams =
          params.entrySet().stream()
              .map(entry -> entry.getKey() + "=" + entry.getValue())
              .collect(Collectors.joining("&"));
      baseUrl = baseUrl + "?" + queryParams;
    }

    RestClient.Builder restClientBuilder = RestClient.builder().baseUrl(baseUrl);

    if (virtualThreadEnabled) {
      Executor virtualThreadExecutor = Executors.newVirtualThreadPerTaskExecutor();
      HttpClient httpClient = HttpClient.newBuilder().executor(virtualThreadExecutor).build();
      restClientBuilder.requestFactory(new JdkClientHttpRequestFactory(httpClient));
    }

    if (headers != null) {
      headers.forEach(restClientBuilder::defaultHeader);
    }

    restClientBuilder.requestInterceptor(new RetryClientHttpRequestInterceptor());

    // 로깅 인터셉터 추가 (필요하다면 주석 해제)
    // restClientBuilder.requestInterceptor(new LoggingInterceptor());

    RestClient restClient = restClientBuilder.build();
    RestClientAdapter adapter = RestClientAdapter.create(restClient);
    HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();

    return factory.createClient(clientClass);
  }
}
