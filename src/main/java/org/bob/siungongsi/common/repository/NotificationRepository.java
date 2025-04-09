package org.bob.siungongsi.common.repository;

import java.util.List;
import java.util.Optional;

import org.bob.siungongsi.common.domain.NotiHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<NotiHistoryEntity, Long> {
  boolean existsByUserIdAndCompanyId(Long userId, Long companyId);

  @Modifying
  void deleteByUserIdAndCompanyId(Long userId, Long companyId);

  Long countByCompanyId(Long companyId);

  @Query(
      "SELECT n.companyId FROM NotiHistoryEntity n GROUP BY n.companyId ORDER BY COUNT(n.companyId) DESC LIMIT 5")
  List<Long> findTop5Companies();

  @Modifying
  @Query("DELETE FROM NotiHistoryEntity n WHERE n.userId = :userId")
  void deleteAllByUserId(@Param("userId") Long userId);

  List<NotiHistoryEntity> findByUserId(Long userId);

  Long countByUserId(Long userId);

  List<NotiHistoryEntity> findByCompanyId(Long companyId);

  // 원자적 쿼리 추가 - 체크와 삽입을 한 번에 수행
  @Modifying
  @Query(
      value =
          "INSERT INTO noti_histories (user_id, company_id, created_dt) "
              + "SELECT :userId, :companyId, NOW() "
              + "FROM dual "
              + "WHERE (SELECT COUNT(*) FROM noti_histories WHERE user_id = :userId) < 10 "
              + "AND NOT EXISTS (SELECT 1 FROM noti_histories WHERE user_id = :userId AND company_id = :companyId)",
      nativeQuery = true)
  int insertIfUnderLimit(@Param("userId") Long userId, @Param("companyId") Long companyId);

  // 삽입된 엔티티 조회를 위한 메서드
  Optional<NotiHistoryEntity> findByUserIdAndCompanyId(Long userId, Long companyId);
}
