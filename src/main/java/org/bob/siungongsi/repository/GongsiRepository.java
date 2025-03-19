package org.bob.siungongsi.repository;

import java.time.LocalDate;

import org.bob.siungongsi.domain.CompanyEntity;
import org.bob.siungongsi.domain.GongsiEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GongsiRepository extends JpaRepository<GongsiEntity, Long> {

  @Query(
      "SELECT g FROM GongsiEntity g WHERE g.company = :company "
          + "AND (:startDate IS NULL OR CAST(g.createdDt AS LocalDate) >= :startDate) "
          + "AND (:endDate IS NULL OR CAST(g.createdDt AS LocalDate) <= :endDate)")
  Page<GongsiEntity> findByCompanyAndDateRange(
      @Param("company") CompanyEntity company,
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate,
      Pageable pageable);

  @Query(
      "SELECT g FROM GongsiEntity g WHERE "
          + "(:startDate IS NULL OR CAST(g.createdDt AS LocalDate) >= :startDate) "
          + "AND (:endDate IS NULL OR CAST(g.createdDt AS LocalDate) <= :endDate)")
  Page<GongsiEntity> findByDateRange(
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate,
      Pageable pageable);
}
