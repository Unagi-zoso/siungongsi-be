package org.bob.siungongsi.common.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "user_agreed_terms")
public class UserAgreedTermEntity extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "term_id", nullable = false)
  private Long termId;

  public UserAgreedTermEntity(Long userId, Long termId) {
    this.userId = userId;
    this.termId = termId;
  }

  public UserAgreedTermEntity() {}

  public Long getId() {
    return id;
  }
}
