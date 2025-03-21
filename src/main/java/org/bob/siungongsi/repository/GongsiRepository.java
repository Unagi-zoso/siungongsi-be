package org.bob.siungongsi.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.bob.siungongsi.domain.CompanyEntity;
import org.bob.siungongsi.domain.GongsiEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GongsiRepository extends JpaRepository<GongsiEntity, Long> {

  Optional<GongsiEntity> findById(Long id);

  @Query(
      "SELECT g FROM GongsiEntity g WHERE g.company = :company "
          + "AND (:startDate IS NULL OR DATE(g.createdDt) >= :startDate) "
          + "AND (:endDate IS NULL OR DATE(g.createdDt) <= :endDate)")
  Page<GongsiEntity> findByCompanyAndDateRange(
      @Param("company") CompanyEntity company,
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate,
      Pageable pageable);

  @Query(
      "SELECT g FROM GongsiEntity g WHERE "
          + "(:startDate IS NULL OR DATE(g.createdDt) >= :startDate) "
          + "AND (:endDate IS NULL OR DATE(g.createdDt) <= :endDate)")
  Page<GongsiEntity> findByDateRange(
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate,
      Pageable pageable);

  // Improved queries for view count sorting
  @Query(
      value =
          "SELECT g.* FROM gongsi g "
              + "LEFT JOIN (SELECT gongsi_id, COUNT(DISTINCT ip_address) as view_count "
              + "          FROM gongsi_view_histories "
              + "          GROUP BY gongsi_id) v ON g.id = v.gongsi_id "
              + "WHERE g.company_id = :companyId "
              + "AND (:startDate IS NULL OR DATE(g.created_dt) >= :startDate) "
              + "AND (:endDate IS NULL OR DATE(g.created_dt) <= :endDate) "
              + "ORDER BY COALESCE(v.view_count, 0) DESC",
      countQuery =
          "SELECT COUNT(*) FROM gongsi g "
              + "WHERE g.company_id = :companyId "
              + "AND (:startDate IS NULL OR DATE(g.created_dt) >= :startDate) "
              + "AND (:endDate IS NULL OR DATE(g.created_dt) <= :endDate)",
      nativeQuery = true)
  Page<GongsiEntity> findByCompanyAndDateRangeOrderByViewCount(
      @Param("companyId") Long companyId,
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate,
      Pageable pageable);

  @Query(
      value =
          "SELECT g.* FROM gongsi g "
              + "LEFT JOIN (SELECT gongsi_id, COUNT(DISTINCT ip_address) as view_count "
              + "          FROM gongsi_view_histories "
              + "          GROUP BY gongsi_id) v ON g.id = v.gongsi_id "
              + "WHERE (:startDate IS NULL OR DATE(g.created_dt) >= :startDate) "
              + "AND (:endDate IS NULL OR DATE(g.created_dt) <= :endDate) "
              + "ORDER BY COALESCE(v.view_count, 0) DESC",
      countQuery =
          "SELECT COUNT(*) FROM gongsi g "
              + "WHERE (:startDate IS NULL OR DATE(g.created_dt) >= :startDate) "
              + "AND (:endDate IS NULL OR DATE(g.created_dt) <= :endDate)",
      nativeQuery = true)
  Page<GongsiEntity> findByDateRangeOrderByViewCount(
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate,
      Pageable pageable);
}
