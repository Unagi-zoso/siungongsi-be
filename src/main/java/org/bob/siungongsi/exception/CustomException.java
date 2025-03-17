package org.bob.siungongsi.exception;

import io.sentry.Sentry;
import org.bob.siungongsi.dto.ApiResponseCode;

public class CustomException extends RuntimeException {
  private final ApiResponseCode errorCode;

  public CustomException(ApiResponseCode errorCode, String message) {
    super(message);
    this.errorCode = errorCode;

    // Sentry에 예외 자동 전송
    Sentry.captureException(this);
  }

  public ApiResponseCode getErrorCode() {
    return errorCode;
  }
}
