package org.bob.siungongsi.repository;

import java.util.List;

import org.bob.siungongsi.domain.TermsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TermsRepository extends JpaRepository<TermsEntity, Integer> {
    List<TermsEntity> findAllByOrderByIdAsc();
}