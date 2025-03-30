package org.bob.siungongsi.repository;

import org.bob.siungongsi.domain.ApiKeyStoreEntity;
import org.springframework.data.repository.CrudRepository;

public interface ApiKeyStoreRepository extends CrudRepository<ApiKeyStoreEntity, Long> {
  ApiKeyStoreEntity findByKeyName(String keyName);
}
