package org.bob.siungongsi.service;

import java.util.HashSet;
import java.util.Set;

import org.bob.siungongsi.repository.CompanyRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/** 프로젝트 초기엔 시총 순위권 기업만을 다루기에 이를 관리하는 클래스 */
@Service
public class RankedCompanyManager {
  private final CompanyRepository companyRepository;
  private final Set<String> rankedCompanyCodes = new HashSet<>();

  public RankedCompanyManager(CompanyRepository companyRepository) {
    this.companyRepository = companyRepository;
    if (rankedCompanyCodes.isEmpty()) {
      companyRepository
          .findAll()
          .forEach(company -> rankedCompanyCodes.add(company.getCompanyCode()));
    }
  }

  @Scheduled(cron = "0 0 0 * * *")
  public void fetchRankedCompanies() {
    rankedCompanyCodes.clear();
    companyRepository
        .findAll()
        .forEach(company -> rankedCompanyCodes.add(company.getCompanyCode()));
  }

  public boolean isRankedCompany(String companyCode) {
    if (rankedCompanyCodes.isEmpty()) {
      companyRepository
          .findAll()
          .forEach(company -> rankedCompanyCodes.add(company.getCompanyCode()));
    }
    return rankedCompanyCodes.contains(companyCode);
  }

  public boolean isNotRankedCompany(String companyCode) {
    return !isRankedCompany(companyCode);
  }
}
