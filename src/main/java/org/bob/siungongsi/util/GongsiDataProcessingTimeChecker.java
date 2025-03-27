package org.bob.siungongsi.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

public class GongsiDataProcessingTimeChecker {
  public static final ZoneId KOREA_ZONE = ZoneId.of("Asia/Seoul"); // 한국 시간
  private static final ZoneId zoneId = KOREA_ZONE;
  private static final LocalTime processingStartTime = LocalTime.of(8, 0);
  private static final LocalTime processingEndTime = LocalTime.of(22, 0);
  private static final boolean excludeWeekends = true;

  private GongsiDataProcessingTimeChecker() {}

  public static boolean isWithinProcessingTime() {
    LocalTime now = LocalTime.now(zoneId);
    LocalDate today = LocalDate.now(zoneId);
    DayOfWeek dayOfWeek = today.getDayOfWeek();

    if (excludeWeekends && (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY)) {
      return false;
    }

    return isAfterOrEqual(now, processingStartTime) && isBeforeOrEqual(now, processingEndTime);
  }

  public static boolean isNotWithinProcessingTime() {
    return !isWithinProcessingTime();
  }

  private static boolean isAfterOrEqual(LocalTime now, LocalTime target) {
    return !now.isBefore(target);
  }

  private static boolean isBeforeOrEqual(LocalTime now, LocalTime target) {
    return !now.isAfter(target);
  }
}
