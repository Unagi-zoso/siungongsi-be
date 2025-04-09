package org.bob.siungongsi.api.controller;

import static org.bob.siungongsi.common.dto.ApiResponseCode.COMPANY_GET_NAME_LIST_SUCCESS;

import org.bob.siungongsi.api.controller.dto.CompanyResponse.CompanyNameListResponse;
import org.bob.siungongsi.api.controller.spec.CompanyControllerSpec;
import org.bob.siungongsi.api.service.CompanyService;
import org.bob.siungongsi.common.dto.ApiResponseCode;
import org.bob.siungongsi.common.dto.ApiResponseWrapper;
import org.bob.siungongsi.common.exception.CustomException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/companies")
public class CompanyController implements CompanyControllerSpec {
  private final CompanyService companyService;

  public CompanyController(CompanyService companyService) {
    this.companyService = companyService;
  }

  @GetMapping("/name")
  public ResponseEntity<ApiResponseWrapper<CompanyNameListResponse>> getCompanyNames(
      @RequestParam String keyword) {

    if (keyword.length() > 18 || keyword.length() < 1) {
      throw new CustomException(ApiResponseCode.COMPANY_INVALID_KEYWORD_LENGTH);
    }

    CompanyNameListResponse companies = companyService.getCompanyNames(keyword);

    return ResponseEntity.status(ApiResponseCode.COMPANY_GET_NAME_LIST_SUCCESS.getHttpStatus())
        .body(
            ApiResponseWrapper.success(
                COMPANY_GET_NAME_LIST_SUCCESS,
                CompanyNameListResponse.of(
                    companies.companyNameListSize(), companies.companyNameList())));
  }
}
