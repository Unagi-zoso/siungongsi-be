package org.bob.siungongsi.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClientException;

public class RetryClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

  private static final int ATTEMPTS = 2;
  private static final Set<HttpStatus> RETRYABLE_STATUS = Set.of(HttpStatus.TOO_MANY_REQUESTS);

  @Override
  public ClientHttpResponse intercept(
      HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
    ClientHttpResponse response = null;
    for (int i = 0; i < ATTEMPTS; i++) {
      response = execution.execute(request, body);

      if (!RETRYABLE_STATUS.contains(response.getStatusCode())) {
        return response;
      }
    }

    // 응답 본문을 안전하게 문자열로 변환
    String responseBody = "";
    if (response != null && response.getBody() != null) {
      responseBody = convertStreamToString(response.getBody());
    }

    throw new RetriesExceededException(
        String.format(
            "Maximum retry attempts (%d) reached for %s. Last response status: %s, body: %s",
            ATTEMPTS, request.getURI(), response.getStatusCode(), responseBody));
  }

  private String convertStreamToString(InputStream inputStream) throws IOException {
    StringBuilder result = new StringBuilder();
    try (BufferedReader reader =
        new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
      String line;
      while ((line = reader.readLine()) != null) {
        result.append(line).append("\n");
      }
    }
    return result.toString().trim();
  }

  public static class RetriesExceededException extends RestClientException {
    public RetriesExceededException(String msg) {
      super(msg);
    }
  }
}
