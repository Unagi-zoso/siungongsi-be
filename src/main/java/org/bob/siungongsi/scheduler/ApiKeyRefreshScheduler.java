package org.bob.siungongsi.scheduler;

import static org.bob.siungongsi.service.ApiKeyStoreManager.KI_API_KEY_NAME;

import org.bob.siungongsi.client.clientinterface.KoreanInvestmentClient;
import org.bob.siungongsi.domain.ApiKeyStoreEntity;
import org.bob.siungongsi.repository.ApiKeyStoreRepository;
import org.bob.siungongsi.service.ApiKeyStoreManager;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import io.sentry.Sentry;

@Profile("dev")
@Component
public class ApiKeyRefreshScheduler {

  private final ApiKeyStoreRepository apiKeyStoreRepository;

  private final KoreanInvestmentClient koreanInvestmentClient;

  private final ApiKeyStoreManager apiKeyStoreManager;

  public ApiKeyRefreshScheduler(
      ApiKeyStoreRepository apiKeyStoreRepository,
      KoreanInvestmentClient koreanInvestmentClient,
      ApiKeyStoreManager apiKeyStoreManager) {
    this.apiKeyStoreRepository = apiKeyStoreRepository;
    this.koreanInvestmentClient = koreanInvestmentClient;
    this.apiKeyStoreManager = apiKeyStoreManager;
  }

  @Transactional
  @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
  public void fetchAndRefreshAccessToken() {
    try {
      String newKiApiKey = koreanInvestmentClient.fetchApprovalKeyWithDelayedRetry(3, 65);
      ApiKeyStoreEntity apiKeyStore =
          apiKeyStoreRepository
              .findByKeyName(KI_API_KEY_NAME)
              .orElseGet(
                  () ->
                      apiKeyStoreRepository.save(
                          new ApiKeyStoreEntity(KI_API_KEY_NAME, newKiApiKey)));

      apiKeyStore.updateApiKey(newKiApiKey);
      apiKeyStoreManager.loadFromDB();
    } catch (Exception e) {
      Sentry.captureException(e);
      System.err.println(
          "Error fetching and refreshing Korean Investment API key: " + e.getMessage());
      throw new RuntimeException(
          "Failed to fetch and refresh Korean Investment API key: " + e.getMessage());
    }
  }
}
