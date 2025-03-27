package org.bob.siungongsi.repository;

import java.util.List;

import org.bob.siungongsi.domain.GongsiSentStatusEntity;
import org.bob.siungongsi.domain.PushStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GongsiSentStatusRepository extends JpaRepository<GongsiSentStatusEntity, Long> {

  List<GongsiSentStatusEntity> findByStatus(PushStatus status, Pageable pageable);
}
