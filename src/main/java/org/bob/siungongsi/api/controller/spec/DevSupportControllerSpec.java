package org.bob.siungongsi.api.controller.spec;

import org.bob.siungongsi.common.dto.ApiResponseWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "개발 및 로컬 환경 편의 API", description = "개발 및 로컬 환경에서 유용한 기능을 제공하는 API")
public interface DevSupportControllerSpec {

  @Operation(summary = "유저 정보 조회")
  ResponseEntity<ApiResponseWrapper<?>> getUser();

  @Operation(summary = "토큰 발급")
  ResponseEntity<ApiResponseWrapper<?>> getToken(
      @RequestParam("userId") String userId,
      @RequestParam(value = "expirationTime", required = false) Long expirationTime);
}
