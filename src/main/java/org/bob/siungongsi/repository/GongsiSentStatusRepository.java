package org.bob.siungongsi.repository;

import java.util.List;

import org.bob.siungongsi.domain.GongsiSentStatusEntity;
import org.bob.siungongsi.domain.PushStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GongsiSentStatusRepository extends JpaRepository<GongsiSentStatusEntity, Long> {

  @Query("SELECT g FROM GongsiSentStatusEntity g WHERE g.status = :status")
  List<GongsiSentStatusEntity> findByStatus(PushStatus status, Pageable pageable);
}
