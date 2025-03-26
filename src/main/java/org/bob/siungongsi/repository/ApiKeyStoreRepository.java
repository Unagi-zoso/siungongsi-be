package org.bob.siungongsi.repository;

import java.util.Optional;

import org.bob.siungongsi.domain.ApiKeyStoreEntity;
import org.springframework.data.repository.CrudRepository;

public interface ApiKeyStoreRepository extends CrudRepository<ApiKeyStoreEntity, Long> {
  Optional<ApiKeyStoreEntity> findByKeyName(String keyName);
}
