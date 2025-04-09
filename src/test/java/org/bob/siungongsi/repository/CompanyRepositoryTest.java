package org.bob.siungongsi.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import org.bob.siungongsi.common.config.JpaAuditConfig;
import org.bob.siungongsi.common.domain.CompanyEntity;
import org.bob.siungongsi.common.repository.CompanyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@Import(JpaAuditConfig.class)
@ActiveProfiles("test")
@DataJpaTest
class CompanyRepositoryTest {

  @Autowired private CompanyRepository repository;

  @Test
  @DisplayName("기업코드로 조회하면 해당 기업이 반환된다")
  void givenCompanyCode_whenFindByCompanyCode_thenReturnsEntity() {
    // given
    CompanyEntity saved = repository.save(new CompanyEntity("테스트전자", "123456", "999999"));

    // when
    CompanyEntity found = repository.findByCompanyCode(saved.getCompanyCode());

    // then
    assertThat(found).isNotNull();
    assertThat(found.getCompanyName()).isEqualTo("테스트전자");
  }

  @Test
  @DisplayName("ID 목록으로 조회하면 해당 기업 목록이 반환된다")
  void givenCompanyIds_whenFindByIdIn_thenReturnsMatchingEntities() {
    // given
    CompanyEntity company1 = repository.save(new CompanyEntity("A전자", "000001", "000001"));
    CompanyEntity company2 = repository.save(new CompanyEntity("B전자", "000002", "000002"));

    // when
    List<CompanyEntity> results =
        repository.findByIdIn(List.of(company1.getId(), company2.getId()));

    // then
    assertThat(results).hasSize(2);
    assertThat(results)
        .extracting(CompanyEntity::getCompanyName)
        .containsExactlyInAnyOrder(company1.getCompanyName(), company2.getCompanyName());
  }

  @Test
  @DisplayName("기업명 오름차순 정렬로 상위 5개를 조회하면 정확한 결과를 반환한다")
  void givenCompanies_whenFindTop5ByOrderByCompanyNameAsc_thenReturnsTop5() {
    // given
    repository.save(new CompanyEntity("전자1", "001", "000001"));
    repository.save(new CompanyEntity("전자3", "003", "000003"));
    repository.save(new CompanyEntity("전자2", "002", "000002"));
    repository.save(new CompanyEntity("전자5", "005", "000005"));
    repository.save(new CompanyEntity("전자4", "004", "000004"));
    repository.save(new CompanyEntity("전자6", "006", "000006"));

    // when
    List<CompanyEntity> results = repository.findTop5ByOrderByCompanyNameAsc();

    // then
    assertThat(results).hasSize(5);
    assertThat(results).isSortedAccordingTo(Comparator.comparing(CompanyEntity::getCompanyName));
  }

  @Test
  @DisplayName("ID가 존재할 경우 existsById는 true를 반환한다")
  void givenExistingId_whenExistsById_thenReturnsTrue() {
    // given
    CompanyEntity saved = repository.save(new CompanyEntity("확인전자", "789456", "000000"));

    // when
    boolean exists = repository.existsById(saved.getId());

    // then
    assertThat(exists).isTrue();
  }

  @Test
  @DisplayName("생성일시로 조회하면 해당 기간에 생성된 기업들을 반환한다")
  void givenCreatedDateRange_whenFindByCreatedDtBetween_thenReturnsMatchingEntities() {
    // given
    LocalDateTime now = LocalDateTime.now();
    CompanyEntity recent = repository.save(new CompanyEntity("새전자", "321654", "000222"));

    // when
    List<CompanyEntity> results =
        repository.findByCreatedDtBetween(now.minusMinutes(1), now.plusMinutes(1));

    // then
    assertThat(results).isNotEmpty();
    assertThat(results).extracting(CompanyEntity::getCompanyName).contains(recent.getCompanyName());
  }
}
