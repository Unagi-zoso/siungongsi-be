package org.bob.siungongsi.batch.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.bob.siungongsi.batch.client.clientinterface.OpenDartClientInterface;
import org.bob.siungongsi.batch.client.dto.OpenDartDtos.GongsiData;
import org.bob.siungongsi.batch.client.dto.OpenDartDtos.GongsiListResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("batch")
@Component
public class OpenDartReader {
  private static final int PAGE_SIZE = 100;
  private final OpenDartClientInterface openDartClient;
  private final TodayProcessedGongsiManager todayProcessedGongsiManager;
  private final RankedCompanyManager rankedCompanyManager;

  public OpenDartReader(
      OpenDartClientInterface openDartClient,
      TodayProcessedGongsiManager todayProcessedGongsiManager,
      RankedCompanyManager rankedCompanyManager) {
    this.openDartClient = openDartClient;
    this.todayProcessedGongsiManager = todayProcessedGongsiManager;
    this.rankedCompanyManager = rankedCompanyManager;
  }

  public List<GongsiData> fetchNoticesWithPagination(LocalDate beginDate, LocalDate endDate) {
    List<GongsiData> allNotices = new ArrayList<>();
    int page = 1;

    while (true) {
      final int currentPage = page;

      GongsiListResponse response =
          openDartClient.getOpenDartList(
              beginDate.toString().replace("-", ""),
              endDate.toString().replace("-", ""),
              currentPage,
              PAGE_SIZE);

      if (response == null
          || response.list() == null
          || response.list().isEmpty()
          || currentPage > response.totalPage()) {
        break;
      }

      for (GongsiData gongsiData : response.list()) {

        if (todayProcessedGongsiManager.isProcessed(gongsiData.rceptNo())
            || rankedCompanyManager.isNotRankedCompany(gongsiData.corpCode())) {
          continue;
        }

        todayProcessedGongsiManager.addProcessedGongsi(gongsiData.rceptNo());
        allNotices.add(gongsiData);
      }

      page++;
    }

    return allNotices;
  }

  public byte[] fetchGongsiDocument(String gongsiId) {
    return openDartClient.getOpenDartDocument(gongsiId);
  }
}
