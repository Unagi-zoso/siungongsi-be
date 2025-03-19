package org.bob.siungongsi.repository;

import java.util.List;

import org.bob.siungongsi.domain.NotiHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<NotiHistoryEntity, Long> {
  boolean existsByUserIdAndCompanyId(Long userId, Long companyId);

  void deleteByUserIdAndCompanyId(Long userId, Long companyId);

  Long countByCompanyId(Long companyId);

  @Query(
      "SELECT n.companyId FROM NotiHistoryEntity n GROUP BY n.companyId ORDER BY COUNT(n.companyId) DESC LIMIT 5")
  List<Long> findTop5Companies();
}
