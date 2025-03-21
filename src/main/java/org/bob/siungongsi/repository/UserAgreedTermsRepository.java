package org.bob.siungongsi.repository;

import org.bob.siungongsi.domain.UserAgreedTermsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAgreedTermsRepository extends JpaRepository<UserAgreedTermsEntity, Long> {
  @Modifying
  @Query("DELETE FROM UserAgreedTermsEntity u WHERE u.userId = :userId")
  void deleteAllByUserId(@Param("userId") Long userId);
}
