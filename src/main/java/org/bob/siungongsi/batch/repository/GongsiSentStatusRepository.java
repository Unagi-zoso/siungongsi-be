package org.bob.siungongsi.batch.repository;

import java.util.List;

import org.bob.siungongsi.batch.domain.GongsiSentStatusEntity;
import org.bob.siungongsi.batch.domain.PushStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GongsiSentStatusRepository extends JpaRepository<GongsiSentStatusEntity, Long> {

  List<GongsiSentStatusEntity> findByStatus(PushStatus status, Pageable pageable);
}
