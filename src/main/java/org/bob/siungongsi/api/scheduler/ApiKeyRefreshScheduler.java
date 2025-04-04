package org.bob.siungongsi.api.scheduler;

import static java.time.LocalDate.now;
import static org.bob.siungongsi.api.service.ApiKeyStoreManager.KI_API_KEY_NAME;

import java.time.ZoneId;

import org.bob.siungongsi.api.client.clientinterface.KoreanInvestmentClient;
import org.bob.siungongsi.api.service.ApiKeyStoreManager;
import org.bob.siungongsi.common.domain.ApiKeyStoreEntity;
import org.bob.siungongsi.common.repository.ApiKeyStoreRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import io.sentry.Sentry;

@Profile("prod")
@Component
public class ApiKeyRefreshScheduler {

  private final ApiKeyStoreRepository apiKeyStoreRepository;

  private final KoreanInvestmentClient koreanInvestmentClient;

  private final ApiKeyStoreManager apiKeyStoreManager;

  private static final ZoneId KOREA_ZONE = ZoneId.of("Asia/Seoul");

  public ApiKeyRefreshScheduler(
      ApiKeyStoreRepository apiKeyStoreRepository,
      KoreanInvestmentClient koreanInvestmentClient,
      ApiKeyStoreManager apiKeyStoreManager) {
    this.apiKeyStoreRepository = apiKeyStoreRepository;
    this.koreanInvestmentClient = koreanInvestmentClient;
    this.apiKeyStoreManager = apiKeyStoreManager;
  }

  @Transactional
  @Scheduled(fixedDelay = 300000) // 5분 (300,000ms)
  public void fetchAndRefreshAccessToken() {
    try {
      ApiKeyStoreEntity apiKeyStore = apiKeyStoreRepository.findByKeyName(KI_API_KEY_NAME);

      if (apiKeyStore != null
          && apiKeyStore.getUpdatedDt().toLocalDate().isEqual(now(KOREA_ZONE))) {
        return;
      }

      String apiKey = koreanInvestmentClient.fetchApprovalKeyWithDelayedRetry(3, 65);

      if (apiKeyStore == null) {
        apiKeyStore = new ApiKeyStoreEntity(KI_API_KEY_NAME, apiKey);
        apiKeyStoreRepository.save(apiKeyStore);
      } else {
        apiKeyStore.updateApiKey(apiKey);
      }
      apiKeyStoreManager.loadFromDB();
    } catch (Exception e) {
      Sentry.captureException(e);
      System.err.println(
          "Error fetching and refreshing Korean Investment API key: " + e.getMessage());
    }
  }
}
