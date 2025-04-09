package org.bob.siungongsi.exception;

import org.bob.siungongsi.dto.ApiResponseCode;
import org.bob.siungongsi.dto.ApiResponseWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import io.sentry.Sentry;
import jakarta.validation.ConstraintViolationException;

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

  // 타입 변환 관련 오류 처리 (예: String -> Long 변환 실패)
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ApiResponseWrapper> handleTypeMismatch(
          MethodArgumentTypeMismatchException ex) {
    Sentry.captureException(ex); // Sentry에 예외 전송

    // gongsiId 파라미터에 대한 특별 처리
    if (ex.getName() != null && ex.getName().equals("gongsiId")) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
              .body(ApiResponseWrapper.error(ApiResponseCode.GONGSI_INVALID_GONGSI_ID));
    }

    // 다른 타입 변환 오류에 대한 일반적인 처리
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponseWrapper.error(ApiResponseCode.API_BAD_REQUEST));
  }

  // Bean Validation 예외 처리 (@Positive, @NotNull 등에서 발생하는 예외)
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ApiResponseWrapper> handleConstraintViolation(
          ConstraintViolationException ex) {
    Sentry.captureException(ex); // Sentry에 예외 전송

    // gongsiId 관련 위반인 경우 특별 처리
    if (ex.getMessage() != null && ex.getMessage().contains("gongsiId")) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
              .body(ApiResponseWrapper.error(ApiResponseCode.GONGSI_INVALID_GONGSI_ID));
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

  // 모든 Exception을 처리하는 핸들러 (최종 예외 캐치)
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 500 오류 반환
  public ApiResponseWrapper handleException(Exception ex) {
    Sentry.captureException(ex); // Sentry에 예외 전송
    return ApiResponseWrapper.error(ApiResponseCode.GONGSI_INTERNAL_SERVER_ERROR);
  }
}
