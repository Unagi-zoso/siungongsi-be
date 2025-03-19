package org.bob.siungongsi.repository;

import java.util.List;

import org.bob.siungongsi.domain.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<CompanyEntity, String> {
  CompanyEntity findByCompanyCode(String CompanyCode);

  List<CompanyEntity> findByIdIn(List<Long> companyIds);

  List<CompanyEntity> findTop5ByOrderByCompanyNameAsc();
}
