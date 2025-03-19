package org.bob.siungongsi.controller;

import static org.bob.siungongsi.dto.ApiResponseCode.GONGSI_LIST_SUCCESS;

import org.bob.siungongsi.controller.dto.CompanyResponse.CompanyInfo;
import org.bob.siungongsi.controller.dto.GongsiResponse.GongsiDetailResponse;
import org.bob.siungongsi.controller.dto.GongsiResponse.GongsiInfo;
import org.bob.siungongsi.controller.dto.GongsiResponse.GongsiListResponse;
import org.bob.siungongsi.controller.spec.GongsiControllerSpec;
import org.bob.siungongsi.dto.ApiResponseWrapper;
import org.bob.siungongsi.service.GongsiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Parameter;

@RestController
@RequestMapping("/v1/gongsi") // 공시 API의 기본 경로
public class GongsiController implements GongsiControllerSpec {

  private final GongsiService gongsiService;

  public GongsiController(GongsiService gongsiService) {
    this.gongsiService = gongsiService;
  }

  @Override
  @GetMapping
  public ResponseEntity<ApiResponseWrapper<GongsiListResponse>> getGongsiList(
      @RequestParam(required = false) Long companyId,
      @RequestParam(defaultValue = "latest") String sort,
      @RequestParam(defaultValue = "false") Boolean content,
      @RequestParam(defaultValue = "1") Integer page,
      @RequestParam(defaultValue = "8") Integer size,
      @RequestParam(required = false) String startDate,
      @RequestParam(required = false) String endDate) {

    GongsiListResponse response =
        gongsiService.getGongsiList(companyId, sort, content, page, size, startDate, endDate);

    return ResponseEntity.ok(ApiResponseWrapper.success(GONGSI_LIST_SUCCESS, response));
  }

  @Override
  @GetMapping("/{gongsild}")
  public ResponseEntity<ApiResponseWrapper<GongsiDetailResponse>> getGongsiDetail(
      @Parameter(description = "공시 ID", example = "101", required = true) @PathVariable("gongsild")
          Long gongsild) {

    GongsiInfo gongsi =
        GongsiInfo.of(
            101,
            "삼성전자, 새로운 반도체 기술 발표",
            "25.02.25 16:04",
            1200,
            "삼성전자가 새로운 반도체 기술을 공개하며...",
            "https://hellothere.xxx");

    CompanyInfo company = CompanyInfo.of(1, "삼성전자", 2.43, false);

    GongsiDetailResponse response = GongsiDetailResponse.of(gongsi, company);

    return ResponseEntity.ok(ApiResponseWrapper.success(GONGSI_LIST_SUCCESS, response));
  }
}
