package org.bob.siungongsi.service;

import org.bob.siungongsi.controller.dto.CompanyResponse;
import org.bob.siungongsi.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompanyServiceImpl implements CompanyService {
  private final CompanyRepository companyRepository;

  @Autowired
  public CompanyServiceImpl(CompanyRepository companyRepository) {
    this.companyRepository = companyRepository;
  }

  @Override
  public CompanyResponse.CompanyNameListResponse getCompanyNames(String keyword) {
    return null;
  }
}
