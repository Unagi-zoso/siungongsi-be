package org.bob.siungongsi.common.repository;

import org.bob.siungongsi.common.domain.ApiKeyStoreEntity;
import org.springframework.data.repository.CrudRepository;

public interface ApiKeyStoreRepository extends CrudRepository<ApiKeyStoreEntity, Long> {
  ApiKeyStoreEntity findByKeyName(String keyName);
}
