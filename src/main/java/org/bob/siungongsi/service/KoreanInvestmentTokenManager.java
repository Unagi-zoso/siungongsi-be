package org.bob.siungongsi.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;

@Service
public class KoreanInvestmentTokenManager {

  private final RestTemplate restTemplate;
  private final ObjectMapper objectMapper;

  @Value("${korean.investment.appkey}")
  private String appKey;

  @Value("${korean.investment.secretkey}")
  private String secretKey;

  @Value("${korean.investment.token.url}")
  private String tokenUrl;

  private String accessToken;

  public KoreanInvestmentTokenManager(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
    this.restTemplate = new RestTemplate();
  }

  @PostConstruct
  public void init() {
    refreshAccessToken();
  }

  @Scheduled(cron = "0 0 0 * * *")
  public void scheduledTokenRefresh() {
    refreshAccessToken();
  }

  public void refreshAccessToken() {
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
        System.out.println("Available fields in response: ");
        root.fieldNames().forEachRemaining(fieldName -> System.out.println(" - " + fieldName));
        throw new Exception(
            "Failed to get approval key. Response fields don't match expected format.");
      }
      accessToken = approvalKey;
    } catch (Exception e) {
      System.err.println("Error refreshing Korean Investment API token: " + e.getMessage());

      if (accessToken == null) {
        throw new RuntimeException(
            "Failed to initialize access token. Please check your API credentials.");
      }
    }
  }

  public String getAccessToken() {
    if (accessToken == null) {
      refreshAccessToken();
    }
    return accessToken;
  }
}
