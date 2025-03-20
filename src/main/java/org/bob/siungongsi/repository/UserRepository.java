package org.bob.siungongsi.repository;

import java.util.Optional;

import org.bob.siungongsi.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
  Optional<UserEntity> findBySocialId(String socialId);

  Optional<UserEntity> findByAccessToken(String accessToken);

  @Query("SELECT n.notiFlag FROM UserEntity n WHERE n.id = :id")
  Short findNotiFlagById(@Param("id") Long id);
}
