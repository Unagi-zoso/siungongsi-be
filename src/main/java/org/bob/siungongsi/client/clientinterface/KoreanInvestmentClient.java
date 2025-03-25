package org.bob.siungongsi.client.clientinterface;

import java.util.HashMap;
import java.util.Map;

import org.bob.siungongsi.service.ApiKeyStoreManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class KoreanInvestmentClient {

  private final ObjectMapper objectMapper;
  private final RestTemplate restTemplate;
  private final ApiKeyStoreManager tokenManager;

  @Value("${korean.investment.appkey}")
  private String appKey;

  @Value("${korean.investment.secretkey}")
  private String secretKey;

  @Value("${korean.investment.stock.url}")
  private String stockUrl;

  @Value("${korean.investment.token.url}")
  private String tokenUrl;

  public KoreanInvestmentClient(ObjectMapper objectMapper, ApiKeyStoreManager tokenManager) {
    this.objectMapper = objectMapper;
    this.tokenManager = tokenManager;
    this.restTemplate = new RestTemplate();
  }

  public double getPrdyCtr(String stockCode) {
    try {
      String accessToken = tokenManager.getAccessToken(ApiKeyStoreManager.KI_API_KEY_NAME);
      return fetchStockData(accessToken, stockCode);
    } catch (Exception e) {
      System.err.println("Error fetching prdyCtr from Korean Investment API: " + e.getMessage());
      throw new RuntimeException("Failed to fetch prdyCtr: " + e.getMessage());
    }
  }

  private double fetchStockData(String accessToken, String stockCode) {
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.set("authorization", "Bearer " + accessToken);
      headers.set("appkey", appKey);
      headers.set("appsecret", secretKey);
      headers.set("tr_id", "FHKST01010100");
      headers.set("custtype", "P"); // 개인(P) 타입으로 설정

      String url = stockUrl + "?FID_COND_MRKT_DIV_CODE=J&FID_INPUT_ISCD=" + stockCode;

      HttpEntity<String> entity = new HttpEntity<>(headers);

      ResponseEntity<String> response =
          restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

      JsonNode root = objectMapper.readTree(response.getBody());

      JsonNode outputData = root.path("output");
      String prdyCtrt = outputData.path("prdy_ctrt").asText();

      return Double.parseDouble(prdyCtrt.replaceAll("[^0-9.-]", ""));

    } catch (RestClientException e) {
      System.err.println("Error fetching stock data from Korean Investment API: " + e.getMessage());
      throw new RuntimeException("Failed to fetch stock data: " + e.getMessage());
    } catch (Exception e) {
      System.err.println("Error processing stock data: " + e.getMessage());
      throw new RuntimeException("Failed to process stock data: " + e.getMessage());
    }
  }

  public String fetchApprovalKeyWithDelayedRetry(int maxRetries, int delaySeconds) {
    for (int i = 0; i < maxRetries; i++) {
      try {
        return fetchApprovalKey();
      } catch (Exception e) {
        if (i < maxRetries - 1) {
          try {
            Thread.sleep(delaySeconds * 1000L);
          } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            break;
          }
        }
      }
    }
    throw new IllegalStateException("Failed to fetch Korean Investment API key after retries.");
  }

  private String fetchApprovalKey() {
    try {
      Map<String, String> requestBody = new HashMap<>();
      requestBody.put("grant_type", "client_credentials");
      requestBody.put("appkey", appKey);
      requestBody.put("appsecret", secretKey);

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);

      HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

      ResponseEntity<String> response =
          restTemplate.exchange(tokenUrl, HttpMethod.POST, entity, String.class);

      String responseBody = response.getBody();
      JsonNode root = objectMapper.readTree(responseBody);

      if (root.has("error_code")) {
        throw new Exception(
            "API Error: "
                + root.path("error_code").asText()
                + " - "
                + root.path("error_description").asText());
      }

      String approvalKey = null;
      if (root.has("approval_key")) {
        approvalKey = root.path("approval_key").asText();
      } else if (root.has("access_token")) {
        approvalKey = root.path("access_token").asText();
      } else if (root.has("token")) {
        approvalKey = root.path("token").asText();
      }

      if (approvalKey == null || approvalKey.isEmpty()) {
        root.fieldNames().forEachRemaining(fieldName -> System.out.println(" - " + fieldName));
        throw new Exception(
            "Failed to get approval key. Response fields don't match expected format.");
      }
      return approvalKey;
    } catch (Exception e) {
      System.err.println("Error refreshing Korean Investment API token: " + e.getMessage());

      throw new RuntimeException(
          "Failed to initialize access token. Please check your API credentials.");
    }
  }
}
