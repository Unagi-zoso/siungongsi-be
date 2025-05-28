package org.bob.siungongsi.common.repository;

import java.util.List;

import org.bob.siungongsi.common.domain.CompanyNameAutofillEntity;
import org.bob.siungongsi.common.dto.projection.CompanyNameAutofillProjection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyNameAutofillRepository
    extends JpaRepository<CompanyNameAutofillEntity, Long> {

  List<CompanyNameAutofillProjection.CompanyNameRecord> findTop5ByKeyword(String keyword);
}
