package org.bob.siungongsi.batch.service;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Profile("batch")
@Service
public class TodayProcessedGongsiService {
  private final TodayProcessedGongsiManager todayProcessedGongsiManager;

  public TodayProcessedGongsiService(TodayProcessedGongsiManager todayProcessedGongsiManager) {
    this.todayProcessedGongsiManager = todayProcessedGongsiManager;
  }

  @Transactional
  public void removeGongsiList(List<String> gongsiCodes) {
    gongsiCodes.forEach(todayProcessedGongsiManager::removeProcessedGongsi);
  }
}
