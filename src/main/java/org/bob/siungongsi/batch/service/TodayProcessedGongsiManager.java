package org.bob.siungongsi.batch.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.bob.siungongsi.batch.domain.TodayProcessedGongsiEntity;
import org.bob.siungongsi.batch.repository.TodayProcessedGongsiRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Profile("batch")
@Service
public class TodayProcessedGongsiManager {
  private final TodayProcessedGongsiRepository todayProcessedGongsiRepository;
  private final Set<String> processedGongsiCodes = new HashSet<>();

  public TodayProcessedGongsiManager(
      TodayProcessedGongsiRepository todayProcessedGongsiRepository) {
    this.todayProcessedGongsiRepository = todayProcessedGongsiRepository;
    loadProcessedIdsFromDB();
  }

  @Transactional
  public void addProcessedGongsi(String gongsiCode) {
    if (!processedGongsiCodes.contains(gongsiCode)
        && !todayProcessedGongsiRepository.existsByGongsiCode(gongsiCode)) {
      processedGongsiCodes.add(gongsiCode);
      todayProcessedGongsiRepository.save(new TodayProcessedGongsiEntity(gongsiCode));
    }
  }

  public void removeProcessedGongsi(String gongsiCode) {
    processedGongsiCodes.remove(gongsiCode);
    todayProcessedGongsiRepository.deleteByGongsiCode(gongsiCode);
  }

  public boolean isProcessed(String gongsiCode) {
    return processedGongsiCodes.contains(gongsiCode)
        || todayProcessedGongsiRepository.existsByGongsiCode(gongsiCode);
  }

  @Scheduled(cron = "0 0 0 * * *")
  @Transactional
  public void clearProcessedGongsi() {
    processedGongsiCodes.clear();
    todayProcessedGongsiRepository.deleteAll();
  }

  private void loadProcessedIdsFromDB() {
    List<String> gongsiCodes =
        todayProcessedGongsiRepository.findAll().stream()
            .map(TodayProcessedGongsiEntity::getGongsiCode)
            .collect(Collectors.toList());
    processedGongsiCodes.addAll(gongsiCodes);
  }
}
