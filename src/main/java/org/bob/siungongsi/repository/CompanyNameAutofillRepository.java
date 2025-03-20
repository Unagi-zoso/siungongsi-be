package org.bob.siungongsi.repository;

import java.util.List;

import org.bob.siungongsi.domain.CompanyNameAutofillEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface CompanyNameAutofillRepository
    extends JpaRepository<CompanyNameAutofillEntity, Long> {

  List<CompanyNameAutofillEntity> findByKeyword(@Param("keyword") String keyword);
}
