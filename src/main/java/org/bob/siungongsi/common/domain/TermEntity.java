package org.bob.siungongsi.common.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "terms")
public class TermEntity extends ModifiableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "title", nullable = false, length = 50)
  private String title;

  @Column(name = "content", nullable = false, columnDefinition = "TEXT")
  private String content;

  @Column(name = "required_flag", nullable = false)
  private Integer requiredFlag;

  protected TermEntity() {}

  public Long getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public String getContent() {
    return content;
  }

  public Integer getRequiredFlag() {
    return requiredFlag;
  }
}
