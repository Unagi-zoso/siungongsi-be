package org.bob.siungongsi.service;

import org.bob.siungongsi.controller.dto.CompanyResponse.CompanyNameListResponse;

public interface CompanyService {
  CompanyNameListResponse getCompanyNames(String keyword);
}
