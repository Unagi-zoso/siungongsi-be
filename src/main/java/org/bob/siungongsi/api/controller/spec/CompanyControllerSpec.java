package org.bob.siungongsi.api.controller.spec;

import org.bob.siungongsi.api.controller.dto.CompanyResponse.CompanyNameListResponse;
import org.bob.siungongsi.common.dto.ApiResponseWrapper;
import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "기업 API", description = "기업 도메인 API")
public interface CompanyControllerSpec {

  @Operation(
      summary = "기업명 검색",
      description = "키워드(자음, 모음 조합 단어)를 기반으로 기업명 자동완성 결과를 반환하는 API",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "기업명 자동완성 목록 조회 성공",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CompanyNameListResponse.class),
                    examples = {
                      @ExampleObject(
                          name = "기업명 자동완성 성공",
                          value =
                              "{ \"code\": 4200, \"message\": \"get_company_name_list_success\", \"data\": { \"companyNameListSize\": 2, \"companyNameList\": [ { \"companyId\": 101, \"companyName\": \"삼성전자\" }, { \"companyId\": 102, \"companyName\": \"LG전자\" } ] } }")
                    })),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 검색 키워드",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponseWrapper.class),
                    examples = {
                      @ExampleObject(
                          name = "유효하지 않은 검색 키워드 길이",
                          value = "{ \"code\": 4400, \"message\": \"invalid_keyword_length\" }")
                    })),
        @ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponseWrapper.class),
                    examples = {
                      @ExampleObject(
                          name = "서버 오류",
                          value = "{ \"code\": 4500, \"message\": \"internal_server_error\" }")
                    }))
      })
  ResponseEntity<ApiResponseWrapper<CompanyNameListResponse>> getCompanyNames(
      @Parameter(description = "검색 키워드 (1자 이상 18자 이하)", required = true) String keyword);
}
