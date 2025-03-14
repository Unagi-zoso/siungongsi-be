package org.bob.siungongsi.exception;

import org.bob.siungongsi.dto.ApiResponseCode;

public class CustomException extends RuntimeException {
  private final ApiResponseCode errorCode;

  public CustomException(ApiResponseCode errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
  }

  public ApiResponseCode getErrorCode() {
    return errorCode;
  }
}
