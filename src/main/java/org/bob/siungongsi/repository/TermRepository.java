package org.bob.siungongsi.repository;

import java.util.List;

import org.bob.siungongsi.domain.TermEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TermRepository extends JpaRepository<TermEntity, Long> {
  List<TermEntity> findAllByOrderByIdAsc();

  boolean existsById(Long id);

  @Query("SELECT t.id FROM TermEntity t WHERE t.requiredFlag = 1")
  List<Long> findIdsByRequiredFlag();
}
