package org.bob.siungongsi.client.clientinterface;

import org.bob.siungongsi.service.KoreanInvestmentTokenManager;
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
  private final KoreanInvestmentTokenManager tokenManager;

  @Value("${korean.investment.appkey}")
  private String appKey;

  @Value("${korean.investment.secretkey}")
  private String secretKey;

  @Value("${korean.investment.stock.url}")
  private String stockUrl;

  public KoreanInvestmentClient(
      ObjectMapper objectMapper, KoreanInvestmentTokenManager tokenManager) {
    this.objectMapper = objectMapper;
    this.tokenManager = tokenManager;
    this.restTemplate = new RestTemplate();
  }

  public double getPrdyCtr(String stockCode) {
    try {
      String accessToken = tokenManager.getAccessToken();
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
}
