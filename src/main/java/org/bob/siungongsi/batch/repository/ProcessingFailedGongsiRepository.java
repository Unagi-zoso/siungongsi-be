package org.bob.siungongsi.batch.repository;

import java.util.Optional;

import org.bob.siungongsi.batch.domain.ProcessingFailedGongsiEntity;
import org.springframework.data.repository.CrudRepository;

public interface ProcessingFailedGongsiRepository
    extends CrudRepository<ProcessingFailedGongsiEntity, String> {
  Optional<ProcessingFailedGongsiEntity> findByGongsiCode(String gongsiCode);
}
