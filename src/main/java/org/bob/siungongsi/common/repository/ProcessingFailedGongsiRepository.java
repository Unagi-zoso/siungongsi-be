package org.bob.siungongsi.common.repository;

import java.util.Optional;

import org.bob.siungongsi.common.domain.ProcessingFailedGongsiEntity;
import org.springframework.data.repository.CrudRepository;

public interface ProcessingFailedGongsiRepository
    extends CrudRepository<ProcessingFailedGongsiEntity, String> {
  Optional<ProcessingFailedGongsiEntity> findByGongsiCode(String gongsiCode);
}
