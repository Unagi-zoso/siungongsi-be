package org.bob.siungongsi.exception;

import org.bob.siungongsi.dto.ApiResponseCode;
import org.bob.siungongsi.dto.ApiResponseWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import io.sentry.Sentry;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(CustomException.class)
  public ResponseEntity<ApiResponseWrapper> handleCustomException(CustomException ex) {
    Sentry.captureException(ex); // Sentry에 예외 전송
    ApiResponseCode responseCode = ex.getErrorCode();
    return ResponseEntity.status(responseCode.getHttpStatus())
        .body(ApiResponseWrapper.error(responseCode));
  }

  // IllegalArgumentException 같은 일반적인 예외 처리
  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST) // 400 오류 반환
  public ApiResponseWrapper handleIllegalArgumentException(IllegalArgumentException ex) {
    Sentry.captureException(ex); // Sentry에 예외 전송
    return ApiResponseWrapper.error(ApiResponseCode.GONGSI_INVALID_SORT_TYPE);
  }

  // NullPointerException 같은 예상치 못한 예외 처리
  @ExceptionHandler(NullPointerException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 500 오류 반환
  public ApiResponseWrapper handleNullPointerException(NullPointerException ex) {
    Sentry.captureException(ex); // Sentry에 예외 전송
    return ApiResponseWrapper.error(ApiResponseCode.GONGSI_INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiResponseWrapper> handleHttpMessageNotReadable(
      HttpMessageNotReadableException ex) {
    return ResponseEntity.status(ApiResponseCode.API_BAD_REQUEST.getHttpStatus())
        .body(ApiResponseWrapper.error(ApiResponseCode.API_BAD_REQUEST));
  }

  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<ApiResponseWrapper> handleNoResourceFoundException(
      NoResourceFoundException ex) {
    return ResponseEntity.status(ApiResponseCode.API_NOT_FOUND.getHttpStatus())
        .body(ApiResponseWrapper.error(ApiResponseCode.API_NOT_FOUND));
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ApiResponseWrapper> methodArgumentTypeMismatchException(
      MethodArgumentTypeMismatchException ex) {
    return ResponseEntity.status(ApiResponseCode.API_WRONG_TYPE_REQUEST.getHttpStatus())
        .body(ApiResponseWrapper.error(ApiResponseCode.API_WRONG_TYPE_REQUEST));
  }

  // 모든 Exception을 처리하는 핸들러 (최종 예외 캐치)
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 500 오류 반환
  public ApiResponseWrapper handleException(Exception ex) {
    Sentry.captureException(ex); // Sentry에 예외 전송
    return ApiResponseWrapper.error(ApiResponseCode.GONGSI_INTERNAL_SERVER_ERROR);
  }
}
