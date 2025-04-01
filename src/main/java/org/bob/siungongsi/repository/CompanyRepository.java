package org.bob.siungongsi.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.bob.siungongsi.domain.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<CompanyEntity, Long> {
  CompanyEntity findByCompanyCode(String companyCode);

  List<CompanyEntity> findByIdIn(List<Long> companyIds);

  List<CompanyEntity> findTop5ByOrderByCompanyNameAsc();

  boolean existsById(Long id);

  List<CompanyEntity> findByCreatedDtBetween(
      LocalDateTime createdDtAfter, LocalDateTime createdDtBefore);
}
