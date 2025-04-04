package org.bob.siungongsi.dto;

import org.springframework.http.HttpStatus;

public enum ApiResponseCode {
  // 공시 관련 응답 코드 (1)
  GONGSI_LIST_SUCCESS(HttpStatus.OK, 1200, "get_gongsi_list_success"),
  GONGSI_DETAIL_SUCCESS(HttpStatus.OK, 1201, "get_detail_gongsi_success"),
  GONGSI_INVALID_SORT_TYPE(HttpStatus.BAD_REQUEST, 1400, "invalid_sort_type"),
  GONGSI_INVALID_COMPANY_ID(HttpStatus.BAD_REQUEST, 1401, "invalid_company_id"),
  GONGSI_INVALID_DATE_PAIR(HttpStatus.BAD_REQUEST, 1402, "invalid_date_pair"),
  GONGSI_NOT_FOUND(HttpStatus.NOT_FOUND, 1403, "gongsi_not_found"),
  GONGSI_COMPANY_NOT_FOUND(HttpStatus.NOT_FOUND, 1404, "company_not_found"),
  GONGSI_INVALID_CONTENT(HttpStatus.BAD_REQUEST, 1405, "invalid_content"),
  GONGSI_INVALID_PAGE(HttpStatus.BAD_REQUEST, 1406, "invalid_page"),
  GONGSI_INVALID_SIZE(HttpStatus.BAD_REQUEST, 1407, "invalid_size"),
  GONGSI_INVALID_GONGSI_ID(HttpStatus.BAD_REQUEST, 1408, "invalid_gongsi_id"),
  GONGSI_INVALID_DATE_FORMAT(HttpStatus.BAD_REQUEST, 1409, "invalid_date_format"),
  GONGSI_INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 1500, "internal_server_error"),

  // 인증 관련 응답 코드 (2)
  AUTH_WITHDRAW_USER_SUCCESS(HttpStatus.OK, 2200, "withdraw_user_success"),
  AUTH_GET_TERMS_SUCCESS(HttpStatus.OK, 2201, "get_terms_success"),
  AUTH_LOGIN_SUCCESS(HttpStatus.OK, 2202, "login_success"),
  AUTH_REGISTER_SUCCESS(HttpStatus.CREATED, 2203, "register_success"),
  AUTH_REQUIRED_AUTHORIZATION(HttpStatus.FORBIDDEN, 2400, "required_authorization"),
  AUTH_KAKAO_ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, 2401, "kakao_access_token_expired"),
  AUTH_USER_NOT_FOUND(HttpStatus.NOT_FOUND, 2402, "user_not_found"),
  AUTH_TERMS_NOT_FOUND(HttpStatus.NOT_FOUND, 2403, "terms_not_found"),
  AUTH_USER_ALREADY_EXISTS(HttpStatus.CONFLICT, 2404, "user_already_exists"),
  AUTH_TERMS_ID_NOT_FOUND(HttpStatus.NOT_FOUND, 2405, "terms_id_not_found"),
  AUTH_USER_AGREED_TERMS_ID_ALREADY_EXISTS(
      HttpStatus.CONFLICT, 2406, "user_agreed_terms_id_already_exists"),
  AUTH_REQUIRED_TERMS_NOT_AGREED(HttpStatus.FORBIDDEN, 2407, "required_terms_not_agreed"),
  AUTH_ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, 2408, "access_token_expired"),
  AUTH_ACCESS_TOKEN_MISSING(HttpStatus.BAD_REQUEST, 2409, "access_token_missing"),
  AUTH_ACCESS_TOKEN_MALFORMED(HttpStatus.BAD_REQUEST, 2410, "access_token_malformed"),
  AUTH_ACCESS_TOKEN_UNSUPPORTED(HttpStatus.BAD_REQUEST, 2411, "access_token_unsupported"),
  AUTH_ACCESS_TOKEN_INVALID_SIGNATURE(
      HttpStatus.UNAUTHORIZED, 2412, "access_token_invalid_signature"),
  AUTH_INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 2500, "internal_server_error"),

  // 유저 관련 응답 코드 (3)
  USER_GET_STATUS(HttpStatus.OK, 3200, "get_notification_status"),
  USER_UPDATE_STATUS_SUCCESS(HttpStatus.OK, 3201, "update_notification_status_success"),
  USER_NOTI_WITHDRAWAL_SUCCESS(HttpStatus.OK, 3201, "user_noti_withdrawal_success"),
  USER_SUBSCRIPTIONS_SUCCESS(HttpStatus.OK, 3205, "get_user_subscriptions"),
  USER_REQUIRED_AUTHORIZATION(HttpStatus.FORBIDDEN, 3400, "required_authorization"),
  USER_STATUS_ALREADY_EXIST(HttpStatus.CONFLICT, 3401, "notification_status_already_exist"),
  USER_INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 3500, "internal_server_error"),

  // 회사 관련 응답 코드 (4)
  COMPANY_GET_NAME_LIST_SUCCESS(HttpStatus.OK, 4200, "get_company_name_list_success"),
  COMPANY_INVALID_KEYWORD_LENGTH(HttpStatus.BAD_REQUEST, 4400, "invalid_keyword_length"),
  COMPANY_INVALID_KEYWORD(HttpStatus.BAD_REQUEST, 4401, "invalid_keyword"),
  COMPANY_INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 4500, "internal_server_error"),

  // 알림 관련 응답 코드 (5)
  NOTIFICATION_RECOMMENDED_COMPANY_SUCCESS(HttpStatus.OK, 5200, "get_recommended_company_success"),
  NOTIFICATION_SUBSCRIPTION_SUCCESS(HttpStatus.CREATED, 5201, "notification_subscription_success"),
  NOTIFICATION_UNSUBSCRIBE_SUCCESS(HttpStatus.OK, 5202, "unsubscribe_notification_success"),
  NOTIFICATION_REQUIRED_AUTHORIZATION(HttpStatus.FORBIDDEN, 5400, "required_authorization"),
  NOTIFICATION_INVALID_COMPANY_ID(HttpStatus.BAD_REQUEST, 5401, "invalid_company_id"),
  NOTIFICATION_ALREADY_EXISTS(HttpStatus.CONFLICT, 5402, "notifications_already_exist"),
  NOTIFICATION_REQUIRED_STATUS(HttpStatus.CONFLICT, 5403, "required_notification_status"),
  NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, 5404, "notification_not_found"),
  NOTIFICATION_LIMIT_EXCEEDED(
      HttpStatus.TOO_MANY_REQUESTS, 5405, "maximum_number_of_notifications_exceeded."),
  NOTIFICATION_COMPANY_ID_IS_NULL(HttpStatus.BAD_REQUEST, 5406, "company_id_not_null"),
  NOTIFICATION_USER_NOT_FOUND(HttpStatus.NOT_FOUND, 5407, "user_not_found"),
  NOTIFICATION_CREATION_INCONSISTENCY(
      HttpStatus.INTERNAL_SERVER_ERROR, 5408, "notification_creation_inconsistency"),

  // 외부 API 관련 응답 코드 (6)
  EXTERNAL_API_ERROR(HttpStatus.BAD_GATEWAY, 6400, "external_api_error"),

  // BODY값 없을 때
  API_BAD_REQUEST(HttpStatus.BAD_REQUEST, 700, "api_bad_request"),

  // API KEY STORE 관련 응답코드 (27)
  KEY_NOT_FOUND(HttpStatus.NOT_FOUND, 2700, "key_not_found"),

  // 처리 실패 공시 관련 응답 코드 (123) 클라이언트가 받지 않는 응답은 123부터 시작합니다.
  FAILED_GONGSI_NOT_FOUND(HttpStatus.NOT_FOUND, 123400, "failed_gongsi_not_found"),

  // FCM 관련 응답 코드 (124)
  FCM_INIT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 124000, "fcm_init_error");

  private final HttpStatus httpStatus;
  private final int code;
  private final String message;

  ApiResponseCode(HttpStatus httpStatus, int code, String message) {
    this.httpStatus = httpStatus;
    this.code = code;
    this.message = message;
  }

  public int getCode() {
    return code;
  }

  public String getMessage() {
    return message;
  }

  public HttpStatus getHttpStatus() {
    return httpStatus;
  }
}
