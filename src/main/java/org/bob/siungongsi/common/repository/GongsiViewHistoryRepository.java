package org.bob.siungongsi.common.repository;

import org.bob.siungongsi.common.domain.GongsiViewHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GongsiViewHistoryRepository extends JpaRepository<GongsiViewHistoryEntity, Long> {

  @Query(
      "SELECT COUNT(DISTINCT g.ipAddress) FROM GongsiViewHistoryEntity g WHERE g.gongsiId = :gongsiId")
  int countUniqueViewsByGongsiId(@Param("gongsiId") Long gongsiId);

  boolean existsByGongsiIdAndIpAddress(Long gongsiId, String ipAddress);
}
