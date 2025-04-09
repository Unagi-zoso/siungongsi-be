package org.bob.siungongsi.common.repository;

import java.util.List;

import org.bob.siungongsi.common.domain.CompanyNameAutofillEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyNameAutofillRepository
    extends JpaRepository<CompanyNameAutofillEntity, Long> {

  List<CompanyNameAutofillEntity> findTop5ByKeyword(String keyword);
}
