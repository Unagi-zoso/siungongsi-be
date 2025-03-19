package org.bob.siungongsi.controller.spec;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "관리자 API", description = "관리를 위한 API")
public interface AdminControllerSpec {

  @Operation(summary = "처리된 공시 삭제")
  void removeProcessedGongsiList(String apiKey, List<String> gongsiCodes);

  @Operation(summary = "실패한 공시 재시도")
  void retryFailedGongsiList(String apiKey, List<String> gongsiCodes);
}
