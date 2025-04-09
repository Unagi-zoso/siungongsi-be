package org.bob.siungongsi.api.controller.spec;

import org.bob.siungongsi.api.controller.dto.GongsiResponse.GongsiDetailResponse;
import org.bob.siungongsi.api.controller.dto.GongsiResponse.GongsiListResponse;
import org.bob.siungongsi.common.dto.ApiResponseWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;

@Tag(name = "공시 API", description = "공시 정보를 조회하는 API")
public interface GongsiControllerSpec {

  @Operation(
      summary = "공시 목록 조회",
      description = "공시 목록을 조회하는 API입니다. (메인 페이지, 공시 검색 페이지에서 사용)",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "공시 목록 조회 성공",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponseWrapper.class),
                    examples = {
                      @ExampleObject(
                          name = "공시 목록 조회 성공",
                          value =
                              "{ \"code\": 1200, \"message\": \"get_gongsi_list_success\", \"data\": { \"gongsiListSize\": 2, \"gongsiList\": [ { \"gongsiId\": 1, \"gongsiTitle\": \"삼성 모든 계열사 어쩌구...\", \"companyName\": \"삼성전자\", \"publishedDatetime\": \"25.03.12 16:04\", \"content\": \"느아아악 나는 배고프다\" }, { \"gongsiId\": 2, \"gongsiTitle\": \"삼성 모든 계열사 어쩌구...1\", \"companyName\": \"삼성전자\", \"publishedDatetime\": \"25.03.12 18:04\", \"content\": \"느아아악 나는 배고프다\" } ], \"pagination\": { \"currentPage\": 1, \"totalPages\": 3, \"totalResults\": 25 } } }")
                    })),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 (예: 유효하지 않은 정렬 방식)",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ApiResponseWrapper.class),
                  examples = {
                    @ExampleObject(
                        name = "잘못된 정렬 방식",
                        value = "{ \"code\": 1400, \"message\": \"invalid_sort_type\" }"),
                    @ExampleObject(
                        name = "잘못된 회사 ID",
                        value = "{ \"code\": 1401, \"message\": \"invalid_company_id\" }"),
                    @ExampleObject(
                        name = "잘못된 날짜 쌍",
                        value = "{ \"code\": 1402, \"message\": \"invalid_date_pair\" }")
                  })
            }),
        @ApiResponse(
            responseCode = "404",
            description = "공시 데이터를 찾을 수 없음",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ApiResponseWrapper.class),
                  examples = {
                    @ExampleObject(
                        name = "공시 데이터 없음",
                        value = "{ \"code\": 1403, \"message\": \"gongsi_not_found\" }"),
                    @ExampleObject(
                        name = "회사 데이터 없음",
                        value = "{ \"code\": 1404, \"message\": \"company_not_found\" }")
                  })
            }),
        @ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ApiResponseWrapper.class),
                  examples = {
                    @ExampleObject(
                        name = "서버 오류",
                        value = "{ \"code\": 1500, \"message\": \"internal_server_error\" }")
                  })
            })
      })
  @GetMapping
  ResponseEntity<ApiResponseWrapper<GongsiListResponse>> getGongsiList(
      @Parameter(description = "조회 대상 회사 ID", example = "1") Long companyId,
      @Parameter(description = "정렬 기준 (latest, views, oldest)", example = "latest") String sort,
      @Parameter(description = "내용 포함 여부", example = "false") Boolean includeContent,
      @Parameter(description = "페이지 번호 (1~100)", example = "1") Integer page,
      @Parameter(description = "페이지 크기 (10~100)", example = "8") Integer size,
      @Parameter(description = "시작 날짜 (yyyy-MM-dd)", example = "2025-03-01") String startDate,
      @Parameter(description = "종료 날짜 (yyyy-MM-dd)", example = "2025-03-10") String endDate);

  @Operation(
      summary = "공시 상세 조회",
      description = "특정 공시의 상세 정보를 조회하는 API입니다.",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "공시 상세 조회 성공",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponseWrapper.class),
                    examples = {
                      @ExampleObject(
                          name = "공시 상세 조회 성공",
                          value =
                              "{ \"code\": 1201, \"message\": \"get_detail_gongsi_success\", \"data\": { \"gongsi\": { \"id\": 101, \"title\": \"삼성전자, 새로운 반도체 기술 발표\", \"date\": \"25.02.25 16:04\", \"viewCount\": 1200, \"content\": \"삼성전자가 새로운 반도체 기술을 공개하며...\", \"originalUrl\": \"https://hellothere.xxx\" }, \"company\": { \"id\": 1, \"name\": \"삼성전자\", \"prdyCtr\": 2.43, \"isSubscribed\": false } } }")
                    })),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 (유효하지 않은 gongsild)",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ApiResponseWrapper.class),
                  examples = {
                    @ExampleObject(
                        name = "잘못된 공시 ID",
                        value = "{ \"code\": 1405, \"message\": \"invalid_gongsild\" }")
                  })
            }),
        @ApiResponse(
            responseCode = "404",
            description = "공시 데이터를 찾을 수 없음",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ApiResponseWrapper.class),
                  examples = {
                    @ExampleObject(
                        name = "공시 데이터 없음",
                        value = "{ \"code\": 1403, \"message\": \"gongsi_not_found\" }")
                  })
            }),
        @ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ApiResponseWrapper.class),
                  examples = {
                    @ExampleObject(
                        name = "서버 오류",
                        value = "{ \"code\": 1500, \"message\": \"internal_server_error\" }")
                  })
            })
      })
  ResponseEntity<ApiResponseWrapper<GongsiDetailResponse>> getGongsiDetail(
      @Parameter(description = "공시 ID", example = "101", required = true)
          @Positive(message = "공시 ID는 양수여야 합니다")
          Long gongsiId,
      HttpServletRequest request);
}
