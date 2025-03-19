package org.bob.siungongsi.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
