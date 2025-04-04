package org.bob.siungongsi.common.client;

import java.net.http.HttpClient;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

public class ClientFactory {

  private ClientFactory() {}

  private static final boolean VIRTUAL_THREAD_ENABLED = true;

  public static <T> T createClient(
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

    if (VIRTUAL_THREAD_ENABLED) {
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
