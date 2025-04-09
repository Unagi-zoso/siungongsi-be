package org.bob.siungongsi.batch.scheduler;

import java.util.List;

import org.bob.siungongsi.batch.service.PushNotiService;
import org.bob.siungongsi.common.domain.GongsiSentStatusEntity;
import org.bob.siungongsi.common.domain.PushStatus;
import org.bob.siungongsi.common.repository.GongsiSentStatusRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Profile("batch")
@Component
public class PushNotiWorker {

  private final GongsiSentStatusRepository gongsiSentStatusRepository;
  private final PushNotiService pushNotiService;
  private static final int MAX_RETRIES = 3;

  public PushNotiWorker(
      GongsiSentStatusRepository gongsiSentStatusRepository, PushNotiService pushNotiService) {
    this.gongsiSentStatusRepository = gongsiSentStatusRepository;
    this.pushNotiService = pushNotiService;
  }

  // @Transactional 외부 API에 의존하기에 한 건씩 개별적으로 처리합니다.
  @Scheduled(fixedDelay = 10000) // 10초마다 실행
  public void processPendingPushes() {
    Pageable pageable = PageRequest.of(0, 50);
    List<GongsiSentStatusEntity> pendingNotices =
        gongsiSentStatusRepository.findByStatus(PushStatus.PENDING, pageable);

    for (GongsiSentStatusEntity sentStatus : pendingNotices) {
      boolean success = pushNotiService.sendPushNotification(sentStatus.getGongsi());

      if (success) {
        sentStatus.markAsSent();
      } else {
        if (sentStatus.getRetryCount() < MAX_RETRIES) {
          sentStatus.incrementRetryCount();
        } else {
          sentStatus.markAsFailed();
        }
      }

      gongsiSentStatusRepository.save(sentStatus);
    }
  }
}
