package org.bob.siungongsi.repository;

import org.bob.siungongsi.domain.UserAgreedTermEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAgreedTermRepository extends JpaRepository<UserAgreedTermEntity, Long> {
  Boolean existsByUserIdAndTermId(Long userId, Long termId);
}
