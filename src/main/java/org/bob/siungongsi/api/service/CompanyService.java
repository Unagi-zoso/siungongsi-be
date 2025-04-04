package org.bob.siungongsi.api.service;

import java.util.List;
import java.util.stream.Collectors;

import org.bob.siungongsi.api.controller.dto.CompanyResponse;
import org.bob.siungongsi.common.domain.CompanyNameAutofillEntity;
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
    List<CompanyNameAutofillEntity> companies =
        companyNameAutofillRepository.findTop5ByKeyword(keyword);

    List<CompanyResponse.CompanyNameResponse> companyNameList =
        companies.stream()
            .map(
                autofill ->
                    new CompanyResponse.CompanyNameResponse(
                        autofill.getCompanyId(), autofill.getCompanyName()))
            .collect(Collectors.toList());

    return new CompanyResponse.CompanyNameListResponse(companyNameList.size(), companyNameList);
  }
}
