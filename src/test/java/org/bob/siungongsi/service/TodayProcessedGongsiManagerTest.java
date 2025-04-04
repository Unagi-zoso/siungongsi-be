package org.bob.siungongsi.service;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.bob.siungongsi.batch.domain.TodayProcessedGongsiEntity;
import org.bob.siungongsi.batch.repository.TodayProcessedGongsiRepository;
import org.bob.siungongsi.batch.service.TodayProcessedGongsiManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TodayProcessedGongsiManagerTest {

  private TodayProcessedGongsiRepository repository;
  private TodayProcessedGongsiManager manager;

  @BeforeEach
  void setUp() {
    repository = mock(TodayProcessedGongsiRepository.class);

    // findAll()이 비어있는 상태로 시작
    when(repository.findAll()).thenReturn(List.of());

    manager = new TodayProcessedGongsiManager(repository);
  }

  @Test
  @DisplayName("생성 시 DB에서 공시 목록을 로드한다")
  void whenConstruct_thenLoadFromDBToSet() {
    // given
    List<TodayProcessedGongsiEntity> processedGongsiEntities =
        List.of(new TodayProcessedGongsiEntity("000001"), new TodayProcessedGongsiEntity("000002"));
    TodayProcessedGongsiRepository repoMock = mock(TodayProcessedGongsiRepository.class);
    when(repoMock.findAll()).thenReturn(processedGongsiEntities);

    // when
    TodayProcessedGongsiManager manager = new TodayProcessedGongsiManager(repoMock);

    // then
    assertThat(manager.isProcessed(processedGongsiEntities.get(0).getGongsiCode())).isTrue();
    assertThat(manager.isProcessed(processedGongsiEntities.get(1).getGongsiCode())).isTrue();
  }

  @Test
  @DisplayName("신규 공시코드는 DB에 존재하지 않으면 저장된다")
  void givenNewCode_whenAddProcessedGongsi_thenSaveToDB() {
    String code = "000001";

    when(repository.existsByGongsiCode(code)).thenReturn(false);

    manager.addProcessedGongsi(code);

    verify(repository).save(any(TodayProcessedGongsiEntity.class));
  }

  @Test
  @DisplayName("이미 처리된 공시코드는 저장되지 않는다")
  void givenDuplicateCode_whenAddProcessedGongsi_thenDoNotSave() {
    String code = "000001";

    // 첫 호출은 DB에도 없고 Set에도 없으므로 저장됨
    when(repository.existsByGongsiCode(code)).thenReturn(false);
    manager.addProcessedGongsi(code);

    // 두 번째 호출에서는 Set에 이미 있으므로 저장 X
    manager.addProcessedGongsi(code);

    // save는 한 번만 호출되어야 함
    verify(repository, times(1)).save(any(TodayProcessedGongsiEntity.class));
  }

  @Test
  @DisplayName("공시코드 삭제 시 Set과 DB에서 제거된다")
  void givenCode_whenRemoveProcessedGongsi_thenRemovedFromSetAndDB() {
    String code = "000001";

    when(repository.existsByGongsiCode(code)).thenReturn(false);
    manager.addProcessedGongsi(code);

    manager.removeProcessedGongsi(code);

    verify(repository).deleteByGongsiCode(code);
  }

  @Test
  @DisplayName("처리된 공시 여부는 Set 또는 DB에 존재하면 true를 반환한다")
  void givenCode_whenIsProcessed_thenCheckSetAndDB() {
    String codeInSet = "000001";
    String codeInDB = "000002";
    String codeNone = "000003";

    // Set에 추가
    when(repository.existsByGongsiCode(codeInDB)).thenReturn(true);
    when(repository.existsByGongsiCode(codeNone)).thenReturn(false);

    manager.addProcessedGongsi(codeInSet);

    assertThat(manager.isProcessed(codeInSet)).isTrue();
    assertThat(manager.isProcessed(codeInDB)).isTrue();
    assertThat(manager.isProcessed(codeNone)).isFalse();
  }

  @Test
  @DisplayName("처리된 공시 목록이 존재할 때 처리된 공시 목록을 삭제하면 Set과 DB가 초기화된다")
  void givenProcessedGongsi_whenClearProcessedGongsi_thenClearSetAndDB() {
    String code = "000001";

    when(repository.existsByGongsiCode(code)).thenReturn(false);
    manager.addProcessedGongsi(code);

    manager.clearProcessedGongsi();

    assertThat(manager.isProcessed(code)).isFalse();
    verify(repository).deleteAll();
  }
}
