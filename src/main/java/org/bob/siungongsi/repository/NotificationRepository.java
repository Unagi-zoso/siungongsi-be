package org.bob.siungongsi.repository;

import org.bob.siungongsi.domain.NotiHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<NotiHistoryEntity, Long> {
  boolean existsByUserIdAndCompanyId(Long userId, Long companyId);

  void deleteByUserIdAndCompanyId(Long userId, Long companyId);
}
