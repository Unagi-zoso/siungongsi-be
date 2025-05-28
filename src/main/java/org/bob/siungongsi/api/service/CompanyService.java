package org.bob.siungongsi.api.service;

import java.util.List;

import org.bob.siungongsi.api.controller.dto.CompanyResponse;
import org.bob.siungongsi.common.dto.projection.CompanyNameAutofillProjection;
import org.bob.siungongsi.common.repository.CompanyNameAutofillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompanyService {
  private final CompanyNameAutofillRepository companyNameAutofillRepository;

  @Autowired
  public CompanyService(CompanyNameAutofillRepository companyNameAutofillRepository) {
    this.companyNameAutofillRepository = companyNameAutofillRepository;
  }

  public CompanyResponse.CompanyNameListResponse getCompanyNames(String keyword) {
    List<CompanyNameAutofillProjection.CompanyNameRecord> companyNameRecords =
        companyNameAutofillRepository.findTop5ByKeyword(keyword);

    List<CompanyResponse.CompanyNameResponse> companyNameList =
        companyNameRecords.stream().map(CompanyResponse.CompanyNameResponse::from).toList();

    return CompanyResponse.CompanyNameListResponse.from(companyNameList);
  }
}
