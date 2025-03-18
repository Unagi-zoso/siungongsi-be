package org.bob.siungongsi.controller.dto;

import java.util.List;

import org.bob.siungongsi.controller.dto.CompanyResponse.CompanyInfo;

public class GongsiResponse {

  public record GongsiListResponse(
      List<GongsiItem> gongsiList, int gongsiListSize, PaginationResponse pagination) {
    public static GongsiListResponse of(
        List<GongsiItem> gongsiList, int gongsiListSize, PaginationResponse pagination) {
      return new GongsiListResponse(gongsiList, gongsiListSize, pagination);
    }
  }

  public record GongsiItem(
      long gongsiId,
      String gongsiTitle,
      String companyName,
      String publishedDatetime,
      String content) {
    public static GongsiItem of(
        long gongsiId,
        String gongsiTitle,
        String companyName,
        String publishedDatetime,
        String content) {
      return new GongsiItem(gongsiId, gongsiTitle, companyName, publishedDatetime, content);
    }
  }

  public record GongsiDetailResponse(GongsiInfo gongsi, CompanyInfo company) {
    public static GongsiDetailResponse of(GongsiInfo gongsi, CompanyInfo company) {
      return new GongsiDetailResponse(gongsi, company);
    }
  }

  public record GongsiInfo(
      long id, String title, String date, int viewCount, String content, String originalUrl) {
    public static GongsiInfo of(
        long id, String title, String date, int viewCount, String content, String originalUrl) {
      return new GongsiInfo(id, title, date, viewCount, content, originalUrl);
    }
  }
}
