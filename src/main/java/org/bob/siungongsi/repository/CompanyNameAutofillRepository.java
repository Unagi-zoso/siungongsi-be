package org.bob.siungongsi.repository;

import java.util.List;

import org.bob.siungongsi.model.CompanyNameAutofill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CompanyNameAutofillRepository extends JpaRepository<CompanyNameAutofill, Long> {
  // Or use a custom query with LIKE operator
  @Query("SELECT c FROM CompanyNameAutofill c WHERE TRIM(c.keyword) = :keyword")
  List<CompanyNameAutofill> findByKeyword(@Param("keyword") String keyword);
}
