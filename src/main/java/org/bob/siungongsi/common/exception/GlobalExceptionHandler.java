package org.bob.siungongsi.common.exception;

import java.util.Map;

import org.bob.siungongsi.common.dto.ApiResponseCode;
import org.bob.siungongsi.common.dto.ApiResponseWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import io.sentry.Sentry;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  // 필드명에 따른 응답 코드 매핑
  // 주의: 컨트롤러 파라미터명이 변경될 경우 해당 맵도 함께 수정해야 합니다
  private static final Map<String, ApiResponseCode> fieldToCodeMap =
      Map.of(
          "gongsiId", ApiResponseCode.GONGSI_INVALID_GONGSI_ID,
          "sort", ApiResponseCode.GONGSI_INVALID_SORT_TYPE
          // 필요한 필드 추가
          );

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

  // 타입 변환 관련 오류 처리 (예: String -> Long 변환 실패)
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ApiResponseWrapper> handleTypeMismatch(
      MethodArgumentTypeMismatchException ex) {
    Sentry.captureException(ex);

    String paramName = ex.getName();

    ApiResponseCode code = fieldToCodeMap.get(paramName);
    if (code != null) {
      return ResponseEntity.status(code.getHttpStatus()).body(ApiResponseWrapper.error(code));
    }

    // 기본 처리
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ApiResponseWrapper.error(ApiResponseCode.API_BAD_REQUEST));
  }

  // Bean Validation 예외 처리 (@Positive, @NotNull 등에서 발생하는 예외)
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ApiResponseWrapper> handleConstraintViolation(
      ConstraintViolationException ex) {
    Sentry.captureException(ex); // Sentry에 예외 전송

    for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
      String fullPath = violation.getPropertyPath().toString();
      String[] parts = fullPath.split("\\.");
      String fieldName = parts[parts.length - 1];

      ApiResponseCode code = fieldToCodeMap.get(fieldName);
      if (code != null) {
        return ResponseEntity.status(code.getHttpStatus()).body(ApiResponseWrapper.error(code));
      }
    }

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ApiResponseWrapper.error(ApiResponseCode.API_BAD_REQUEST));
  }

  // Form 검증 예외 처리
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponseWrapper> handleValidationExceptions(
      MethodArgumentNotValidException ex) {
    Sentry.captureException(ex); // Sentry에 예외 전송
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ApiResponseWrapper.error(ApiResponseCode.API_BAD_REQUEST));
  }

  // Form 바인딩 실패 예외 처리
  @ExceptionHandler(BindException.class)
  public ResponseEntity<ApiResponseWrapper> handleBindException(BindException ex) {
    Sentry.captureException(ex); // Sentry에 예외 전송
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ApiResponseWrapper.error(ApiResponseCode.API_BAD_REQUEST));
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiResponseWrapper> handleHttpMessageNotReadable(
      HttpMessageNotReadableException ex) {
    Sentry.captureException(ex); // Sentry에 예외 전송
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ApiResponseWrapper.error(ApiResponseCode.API_BAD_REQUEST));
  }

  @ExceptionHandler(NoHandlerFoundException.class)
  public ResponseEntity<ApiResponseWrapper<Void>> handleNoHandlerFoundException(
      NoHandlerFoundException ex) {
    return ResponseEntity.status(ApiResponseCode.RESOURCE_NOT_FOUND.getHttpStatus())
        .body(ApiResponseWrapper.error(ApiResponseCode.RESOURCE_NOT_FOUND));
  }

  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<ApiResponseWrapper> handleNoResourceFoundException(
      NoResourceFoundException ex) {
    return ResponseEntity.status(ApiResponseCode.API_NOT_FOUND.getHttpStatus())
        .body(ApiResponseWrapper.error(ApiResponseCode.API_NOT_FOUND));
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<ApiResponseWrapper> handleMissingRequestParameter(
      MissingServletRequestParameterException ex) {
    return ResponseEntity.status(ApiResponseCode.API_BAD_REQUEST.getHttpStatus())
        .body(ApiResponseWrapper.error(ApiResponseCode.API_BAD_REQUEST));
  }

  // PathVariable 누락 예외 처리 (예: gongsi/공백)
  @ExceptionHandler(MissingPathVariableException.class)
  public ResponseEntity<ApiResponseWrapper> handleMissingPathVariable(
      MissingPathVariableException ex) {
    return ResponseEntity.status(ApiResponseCode.API_BAD_REQUEST.getHttpStatus())
        .body(ApiResponseWrapper.error(ApiResponseCode.API_BAD_REQUEST));
  }

  // 모든 Exception을 처리하는 핸들러 (최종 예외 캐치)
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 500 오류 반환
  public ApiResponseWrapper handleException(Exception ex) {
    Sentry.captureException(ex); // Sentry에 예외 전송
    return ApiResponseWrapper.error(ApiResponseCode.GONGSI_INTERNAL_SERVER_ERROR);
  }
}
