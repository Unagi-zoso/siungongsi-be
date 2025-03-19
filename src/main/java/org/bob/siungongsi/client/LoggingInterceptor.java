package org.bob.siungongsi.client;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public class LoggingInterceptor implements ClientHttpRequestInterceptor {
  @Override
  public ClientHttpResponse intercept(
      HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

    ClientHttpResponse response = execution.execute(request, body);

    // 응답 로그 출력
    String responseBody = new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8);

    return response;
  }
}
