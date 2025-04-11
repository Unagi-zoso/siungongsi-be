package org.bob.siungongsi.batch.scheduler;

import static org.bob.siungongsi.batch.util.GongsiDataProcessingTimeChecker.isNotWithinProcessingTime;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.bob.siungongsi.batch.client.dto.OpenDartDtos.GongsiData;
import org.bob.siungongsi.batch.event.GongsiMessage;
import org.bob.siungongsi.batch.service.MessageSender;
import org.bob.siungongsi.batch.service.OpenDartReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Profile("batch")
public class GongsiProcessingScheduler {

  private static final Logger logger = LoggerFactory.getLogger(GongsiProcessingScheduler.class);

  private static final ZoneId KOREA_ZONE = ZoneId.of("Asia/Seoul"); // 한국 시간 설정

  private final OpenDartReader openDartReader;
  private final MessageSender messageSender;

  public GongsiProcessingScheduler(OpenDartReader openDartReader, MessageSender messageSender) {
    this.openDartReader = openDartReader;
    this.messageSender = messageSender;
  }

  @Scheduled(fixedRate = 600000) // 10분 (600,000ms)
  public void runGongsiUploadScheduler() {
    if (isNotWithinProcessingTime()) {
      return;
    }

    logger.info("[Gongsi Scheduler] Execution started");

    List<GongsiData> response =
        openDartReader.fetchNoticesWithPagination(
            LocalDate.now(KOREA_ZONE), LocalDate.now(KOREA_ZONE));

    logger.info("[Gongsi Scheduler] Received {} disclosures", response.size());

    for (GongsiData notice : response) {
      CompletableFuture.runAsync(
              () -> {
                messageSender.sendGongsiMessage(GongsiMessage.from(notice));
                logger.info(
                    "[Gongsi Message Sent] receiptNo={}, corpCode={}",
                    notice.rceptNo(),
                    notice.corpCode());
              })
          .exceptionally(
              e -> {
                logger.warn(
                    "[Gongsi Message Failed] receiptNo={}, corpCode={}, error={}",
                    notice.rceptNo(),
                    notice.corpCode(),
                    e.getMessage());
                return null;
              });
    }
    logger.info("[Gongsi Scheduler] Message send attempt completed: total {}", response.size());
  }
}
