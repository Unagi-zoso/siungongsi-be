package org.bob.siungongsi.common.repository;

import java.util.Optional;

import org.bob.siungongsi.common.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
  Optional<UserEntity> findBySocialId(String socialId);

  boolean existsBySocialId(String socialId);

  @Query("SELECT n.notiFlag FROM UserEntity n WHERE n.id = :id")
  boolean findNotiFlagById(@Param("id") Long id);
}
