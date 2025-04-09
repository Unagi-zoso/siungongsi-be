package org.bob.siungongsi.common.repository;

import org.bob.siungongsi.common.domain.UserAgreedTermEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAgreedTermRepository extends JpaRepository<UserAgreedTermEntity, Long> {
  boolean existsByUserIdAndTermId(Long userId, Long termId);

  @Modifying
  @Query("DELETE FROM UserAgreedTermEntity u WHERE u.userId = :userId")
  void deleteAllByUserId(@Param("userId") Long userId);
}
