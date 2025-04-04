package org.bob.siungongsi.batch.repository;

import org.bob.siungongsi.batch.domain.TodayProcessedGongsiEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodayProcessedGongsiRepository
    extends JpaRepository<TodayProcessedGongsiEntity, String> {
  boolean existsByGongsiCode(String gongsiCode);

  void deleteByGongsiCode(String gongsiCode);
}
