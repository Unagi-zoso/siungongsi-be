package org.bob.siungongsi.api.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "이용 약관 응답 DTO")
public record TermsResponse(
    @Schema(description = "약관 ID", example = "1") long termsId,
    @Schema(description = "약관 제목", example = "서비스 이용약관") String termsTitle,
    @Schema(description = "약관 내용", example = "서비스를 이용하기 위해서는 ...") String termsContent) {
  public static TermsResponse of(long termsId, String termsTitle, String termsContent) {
    return new TermsResponse(termsId, termsTitle, termsContent);
  }
}
