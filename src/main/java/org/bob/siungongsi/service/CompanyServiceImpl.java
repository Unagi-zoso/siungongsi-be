package org.bob.siungongsi.service;

import java.util.List;
import java.util.stream.Collectors;

import org.bob.siungongsi.controller.dto.CompanyResponse;
import org.bob.siungongsi.model.CompanyNameAutofill;
import org.bob.siungongsi.repository.CompanyNameAutofillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompanyServiceImpl implements CompanyService {
  private final CompanyNameAutofillRepository companyNameAutofillRepository;

  @Autowired
  public CompanyServiceImpl(CompanyNameAutofillRepository companyNameAutofillRepository) {
    this.companyNameAutofillRepository = companyNameAutofillRepository;
  }

  @Override
  public CompanyResponse.CompanyNameListResponse getCompanyNames(String keyword) {
    List<CompanyNameAutofill> companies = companyNameAutofillRepository.findByKeyword(keyword);

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
