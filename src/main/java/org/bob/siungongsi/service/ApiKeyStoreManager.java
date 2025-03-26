package org.bob.siungongsi.service;

import static org.bob.siungongsi.dto.ApiResponseCode.KEY_NOT_FOUND;

import java.util.HashMap;
import java.util.Map;

import org.bob.siungongsi.domain.ApiKeyStoreEntity;
import org.bob.siungongsi.exception.CustomException;
import org.bob.siungongsi.repository.ApiKeyStoreRepository;
import org.springframework.stereotype.Service;

@Service
public class ApiKeyStoreManager {

  public static final String KI_API_KEY_NAME = "korean_investment_approval";

  private final Map<String, String> tokenMap = new HashMap<>();

  private final ApiKeyStoreRepository apiKeyStoreRepository;

  public ApiKeyStoreManager(ApiKeyStoreRepository apiKeyStoreRepository) {
    this.apiKeyStoreRepository = apiKeyStoreRepository;
  }

  public String getAccessToken(String keyName) {
    if (!tokenMap.containsKey(keyName)) {
      ApiKeyStoreEntity apiKeyStore = apiKeyStoreRepository.findByKeyName(keyName).get();
      String apiKey = apiKeyStore.getApiKey();
      if (apiKey == null) {
        throw new CustomException(KEY_NOT_FOUND, KEY_NOT_FOUND.getMessage());
      }
      tokenMap.put(keyName, apiKey);
    }
    return tokenMap.get(keyName);
  }
}
