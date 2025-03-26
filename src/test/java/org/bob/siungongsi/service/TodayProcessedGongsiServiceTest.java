package org.bob.siungongsi.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TodayProcessedGongsiServiceTest {

  private TodayProcessedGongsiManager todayProcessedGongsiManager;
  private TodayProcessedGongsiService todayProcessedGongsiService;

  @BeforeEach
  void setUp() {
    todayProcessedGongsiManager = mock(TodayProcessedGongsiManager.class);
    todayProcessedGongsiService = new TodayProcessedGongsiService(todayProcessedGongsiManager);
  }

  @Test
  @DisplayName("공시코드 리스트를 받아 각 코드를 삭제한다")
  void givenGongsiCodes_whenRemoveGongsiList_thenEachCodeIsRemoved() {
    // given
    List<String> gongsiCodes = List.of("000001", "000002", "000003");

    // when
    todayProcessedGongsiService.removeGongsiList(gongsiCodes);

    // then
    verify(todayProcessedGongsiManager, times(1)).removeProcessedGongsi(gongsiCodes.get(0));
    verify(todayProcessedGongsiManager, times(1)).removeProcessedGongsi(gongsiCodes.get(1));
    verify(todayProcessedGongsiManager, times(1)).removeProcessedGongsi(gongsiCodes.get(2));
    verifyNoMoreInteractions(todayProcessedGongsiManager);
  }
}
