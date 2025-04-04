package org.bob.siungongsi.api.controller.dto;

import java.util.List;

public class NotificationResponse {

  public record NotificationRecommendedCompany(
      Long companyId, String companyName, Long subscriberCnt, boolean isSubscribed) {
    public static NotificationRecommendedCompany of(
        Long companyId, String companyName, Long subscriberCnt, boolean isSubscribed) {
      return new NotificationRecommendedCompany(
          companyId, companyName, subscriberCnt, isSubscribed);
    }
  }

  public record NotificationRecommendedCompanyList(List<NotificationRecommendedCompany> companies) {
    public static NotificationRecommendedCompanyList of(
        List<NotificationRecommendedCompany> companies) {
      return new NotificationRecommendedCompanyList(companies);
    }
  }
}
