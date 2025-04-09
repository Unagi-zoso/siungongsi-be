package org.bob.siungongsi.batch.client.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OpenDartDtos {

  public record GongsiListResponse(
      @JsonProperty("list") List<GongsiData> list,
      @JsonProperty("total_page") int totalPage,
      @JsonProperty("total_count") int totalSize) {}

  public record GongsiData(
      @JsonProperty("corp_code") String corpCode,
      @JsonProperty("corp_name") String corpName,
      @JsonProperty("report_nm") String reportNm,
      @JsonProperty("rcept_no") String rceptNo,
      @JsonProperty("flr_nm") String flrNm,
      @JsonProperty("rcept_dt") String rceptDt,
      @JsonProperty("rm") String rm) {}
}
