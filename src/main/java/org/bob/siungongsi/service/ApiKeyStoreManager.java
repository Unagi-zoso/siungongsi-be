package org.bob.siungongsi.service;

import static org.bob.siungongsi.dto.ApiResponseCode.KEY_NOT_FOUND;

import java.util.HashMap;
import java.util.Map;

import org.bob.siungongsi.client.clientinterface.KoreanInvestmentClient;
import org.bob.siungongsi.exception.CustomException;
import org.bob.siungongsi.repository.ApiKeyStoreRepository;
import org.springframework.stereotype.Service;

@Service
public class ApiKeyStoreManager {

  public static final String KI_API_KEY_NAME = "korean_investment_approval";

  private final Map<String, String> tokenMap = new HashMap<>();

  private final ApiKeyStoreRepository apiKeyStoreRepository;

  private final KoreanInvestmentClient koreanInvestmentClient;

  public ApiKeyStoreManager(
      ApiKeyStoreRepository apiKeyStoreRepository, KoreanInvestmentClient koreanInvestmentClient) {
    this.apiKeyStoreRepository = apiKeyStoreRepository;
    this.koreanInvestmentClient = koreanInvestmentClient;
  }

  public String getAccessToken(String keyName) {
    if (!tokenMap.containsKey(keyName)) {
      throw new CustomException(KEY_NOT_FOUND, "API key not found: " + keyName);
    }
    return tokenMap.get(keyName);
  }

  public void loadFromDB() {
    apiKeyStoreRepository
        .findAll()
        .forEach(
            apiKeyStore -> {
              tokenMap.put(apiKeyStore.getKeyName(), apiKeyStore.getApiKey());
            });
  }
}
