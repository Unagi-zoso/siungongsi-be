package org.bob.siungongsi.fixture;

public class DevSupportFixture {

  // 응답 코드 및 메시지
  public static final int DEV_SUPPORT_RESPONSE_CODE = 12341243;
  public static final String DEV_SUPPORT_USER_MESSAGE = "토큰 획득용 유저 정보";
  public static final String DEV_SUPPORT_TOKEN_MESSAGE = "토큰 획득";

  // JWT 토큰 테스트 데이터
  public static final String TEST_JWT_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.token";
  public static final String TEST_JWT_TOKEN_WITH_EXPIRATION =
      "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.token.with.expiration";
  public static final String TEST_JWT_TOKEN_DEFAULT = "default.expiration.token";

  // 토큰 만료 시간 (밀리초)
  public static final Long DEFAULT_EXPIRATION_TIME = 3600000L; // 1 hour
  public static final Long CUSTOM_EXPIRATION_TIME = 7200000L; // 2 hours
  public static final Long SHORT_EXPIRATION_TIME = 600000L; // 10 minutes
}
