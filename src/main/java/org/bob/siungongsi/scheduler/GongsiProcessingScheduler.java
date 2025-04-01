package org.bob.siungongsi.scheduler;

import static org.bob.siungongsi.util.GongsiDataProcessingTimeChecker.isNotWithinProcessingTime;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.bob.siungongsi.client.OpenDartReader;
import org.bob.siungongsi.client.dto.OpenDartDtos.GongsiData;
import org.bob.siungongsi.event.GongsiMessage;
import org.bob.siungongsi.service.MessageSender;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Profile("batch")
public class GongsiProcessingScheduler {

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

    List<GongsiData> response =
        openDartReader.fetchNoticesWithPagination(
            LocalDate.now(KOREA_ZONE), LocalDate.now(KOREA_ZONE));

    for (GongsiData notice : response) {
      CompletableFuture.runAsync(
          () -> {
            messageSender.sendGongsiMessage(GongsiMessage.from(notice));
          });
    }
  }
}
