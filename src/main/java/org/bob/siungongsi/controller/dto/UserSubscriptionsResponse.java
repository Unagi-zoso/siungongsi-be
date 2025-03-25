package org.bob.siungongsi.controller.dto;

import java.util.List;

public record UserSubscriptionsResponse(Long userId, List<SubscribedCompany> subscribedCompanies) {
  public static UserSubscriptionsResponse of(
      Long userId, List<SubscribedCompany> subscribedCompanies) {
    return new UserSubscriptionsResponse(userId, subscribedCompanies);
  }

  public record SubscribedCompany(
      Long companyId, String companyName, String companyCode, String stockCode) {
    public static SubscribedCompany of(
        Long companyId, String companyName, String companyCode, String stockCode) {
      return new SubscribedCompany(companyId, companyName, companyCode, stockCode);
    }
  }
}
