package org.bob.siungongsi.repository;

import java.util.List;

import org.bob.siungongsi.domain.CompanyNameAutofillEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyNameAutofillRepository
    extends JpaRepository<CompanyNameAutofillEntity, Long> {

  List<CompanyNameAutofillEntity> findTop5ByKeyword(String keyword);
}
