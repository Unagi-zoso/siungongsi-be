package org.bob.siungongsi.repository;

import org.bob.siungongsi.domain.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<CompanyEntity, String> {
  CompanyEntity findByCompanyCode(String CompanyCode);
}
