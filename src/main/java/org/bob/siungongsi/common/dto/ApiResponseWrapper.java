package org.bob.siungongsi.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponseWrapper<T>(int code, String message, T data) {

  // 성공 응답
  public static <T> ApiResponseWrapper<T> success(ApiResponseCode responseCode, T data) {
    return new ApiResponseWrapper<>(responseCode.getCode(), responseCode.getMessage(), data);
  }

  // 성공 응답 (data가 없을 때는 data를 생략)
  public static <T> ApiResponseWrapper<T> success(ApiResponseCode responseCode) {
    return new ApiResponseWrapper<>(responseCode.getCode(), responseCode.getMessage(), null);
  }

  // 실패 응답 (에러 응답)
  public static <T> ApiResponseWrapper<T> error(ApiResponseCode responseCode) {
    return new ApiResponseWrapper<>(responseCode.getCode(), responseCode.getMessage(), null);
  }

  // 특정 상태 코드와 메시지로 에러 응답 - 직접 작성
  public static <T> ApiResponseWrapper<T> error(int code, String message) {
    return new ApiResponseWrapper<>(code, message, null);
  }
}
