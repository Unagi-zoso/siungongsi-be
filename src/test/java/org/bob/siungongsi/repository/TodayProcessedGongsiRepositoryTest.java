package org.bob.siungongsi.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.bob.siungongsi.config.JpaAuditConfig;
import org.bob.siungongsi.domain.TodayProcessedGongsiEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@Import(JpaAuditConfig.class)
@ActiveProfiles("test")
@DataJpaTest
class TodayProcessedGongsiRepositoryTest {

  @Autowired private TodayProcessedGongsiRepository repository;

  @Test
  @DisplayName("공시코드가 존재하는 경우 existsByGongsiCode는 true를 반환한다")
  void givenExistingGongsiCode_whenExistsByGongsiCode_thenReturnsTrue() {
    // given
    String code = "000001";
    repository.save(new TodayProcessedGongsiEntity(code));

    // when
    boolean exists = repository.existsByGongsiCode(code);

    // then
    assertThat(exists).isTrue();
  }

  @Test
  @DisplayName("공시코드가 존재하지 않는 경우 existsByGongsiCode는 false를 반환한다")
  void givenNonExistingGongsiCode_whenExistsByGongsiCode_thenReturnsFalse() {
    // given
    String nonExistingCode = "999999";

    // when
    boolean exists = repository.existsByGongsiCode(nonExistingCode);

    // then
    assertThat(exists).isFalse();
  }

  @Test
  @DisplayName("공시코드로 삭제하면 해당 엔티티는 삭제된다")
  void givenExistingGongsiCode_whenDeleteByGongsiCode_thenEntityIsDeleted() {
    // given
    String code = "000002";
    repository.save(new TodayProcessedGongsiEntity(code));

    // when
    repository.deleteByGongsiCode(code);

    // then
    boolean exists = repository.existsByGongsiCode(code);
    assertThat(exists).isFalse();
  }
}
