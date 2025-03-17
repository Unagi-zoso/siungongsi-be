package org.bob.siungongsi.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TodayProcessedGongsiRepository
    extends JpaRepository<TodayProcessedGongsiEntity, String> {
  boolean existsByGongsiCode(String gongsiCode);
}
