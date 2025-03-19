package org.bob.siungongsi.repository;

import org.bob.siungongsi.domain.TodayProcessedGongsiEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodayProcessedGongsiRepository
    extends JpaRepository<TodayProcessedGongsiEntity, String> {
  boolean existsByGongsiCode(String gongsiCode);

  void deleteByGongsiCode(String gongsiCode);
}
