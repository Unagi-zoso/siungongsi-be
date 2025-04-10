package org.bob.siungongsi.batch.controller.spec;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "배치 환경 편의 API", description = "배치 환경에서 유용한 기능을 제공하는 API")
public interface BatchSupportControllerSpec {

  @Operation(summary = "처리된 공시 삭제")
  void removeProcessedGongsiList(String apiKey, List<String> gongsiCodes);

  @Operation(summary = "실패한 공시 재시도")
  void retryFailedGongsiList(String apiKey, List<String> gongsiCodes);

  @Operation(summary = "회사명 자동완성")
  void autofillCompanyName(String apiKey, String startDt, String endDt);
}
