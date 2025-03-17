package org.bob.siungongsi.repository;

import java.util.List;

import org.bob.siungongsi.domain.CompanyNameAutofillEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CompanyNameAutofillRepository
    extends JpaRepository<CompanyNameAutofillEntity, Long> {
  // Or use a custom query with LIKE operator
  @Query("SELECT c FROM CompanyNameAutofillEntity c WHERE TRIM(c.keyword) = :keyword")
  List<CompanyNameAutofillEntity> findByKeyword(@Param("keyword") String keyword);
}
