package org.bob.siungongsi.repository;

import java.util.Optional;

import org.bob.siungongsi.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
  Optional<UserEntity> findBySocialId(String socialId);

  Optional<UserEntity> findByAccessToken(String accessToken);
}
