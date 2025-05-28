package org.bob.siungongsi.api.controller.dto;

import java.util.List;

import org.bob.siungongsi.common.dto.projection.CompanyNameAutofillProjection;

public class CompanyResponse {
  public record CompanyNameListResponse(
      int companyNameListSize, List<CompanyNameResponse> companyNameList) {
    public static CompanyNameListResponse from(List<CompanyNameResponse> companyNameList) {
      return new CompanyNameListResponse(companyNameList.size(), companyNameList);
    }
  }

  public record CompanyNameResponse(long companyId, String companyName) {
    public static CompanyNameResponse from(
        CompanyNameAutofillProjection.CompanyNameRecord companyNameRecord) {
      return new CompanyNameResponse(
          companyNameRecord.companyId(), companyNameRecord.companyName());
    }
  }

  public record CompanyInfo(long id, String name, double prdyCtr, boolean isSubscribed) {
    public static CompanyInfo of(long id, String name, double prdyCtr, boolean isSubscribed) {
      return new CompanyInfo(id, name, prdyCtr, isSubscribed);
    }
  }
}
