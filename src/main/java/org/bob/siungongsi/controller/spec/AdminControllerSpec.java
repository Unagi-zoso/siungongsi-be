package org.bob.siungongsi.controller.spec;

import java.util.List;

import org.bob.siungongsi.dto.ApiResponseWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "관리자 API", description = "관리를 위한 API")
public interface AdminControllerSpec {

  @Operation(summary = "처리된 공시 삭제")
  void removeProcessedGongsiList(String apiKey, List<String> gongsiCodes);

  @Operation(summary = "실패한 공시 재시도")
  void retryFailedGongsiList(String apiKey, List<String> gongsiCodes);

  @Operation(summary = "회사명 자동완성")
  void autofillCompanyName(String apiKey, String startDt, String endDt);

  @Operation(summary = "유저 정보 조회")
  ResponseEntity<ApiResponseWrapper<?>> getUser();

  @Operation(summary = "토큰 발급")
  public ResponseEntity<ApiResponseWrapper<?>> getToken(@RequestParam("userId") String userId);
}
