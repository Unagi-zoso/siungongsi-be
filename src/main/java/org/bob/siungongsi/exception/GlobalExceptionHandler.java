package org.bob.siungongsi.exception;

import org.bob.siungongsi.dto.ApiResponseCode;
import org.bob.siungongsi.dto.ApiResponseWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  // CustomException을 처리하는 핸들러
  @ExceptionHandler(CustomException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST) // 400 오류 반환
  public ApiResponseWrapper handleCustomException(CustomException ex) {
    return ApiResponseWrapper.error(ex.getErrorCode());
  }

  // IllegalArgumentException 같은 일반적인 예외 처리
  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST) // 400 오류 반환
  public ApiResponseWrapper handleIllegalArgumentException(IllegalArgumentException ex) {
    return ApiResponseWrapper.error(ApiResponseCode.GONGSI_INVALID_SORT_TYPE);
  }

  // NullPointerException 같은 예상치 못한 예외 처리
  @ExceptionHandler(NullPointerException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 500 오류 반환
  public ApiResponseWrapper handleNullPointerException(NullPointerException ex) {
    return ApiResponseWrapper.error(ApiResponseCode.GONGSI_INTERNAL_SERVER_ERROR);
  }

  // 모든 Exception을 처리하는 핸들러 (최종 예외 캐치)
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 500 오류 반환
  public ApiResponseWrapper handleException(Exception ex) {
    return ApiResponseWrapper.error(ApiResponseCode.GONGSI_INTERNAL_SERVER_ERROR);
  }
}
