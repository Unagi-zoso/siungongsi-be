package org.bob.siungongsi.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.bob.siungongsi.fixture.AuthFixture.TEST_KAKAO_ACCESS_TOKEN;
import static org.bob.siungongsi.fixture.AuthFixture.TEST_SOCIAL_ID;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import org.bob.siungongsi.api.service.KakaoAuthService.KakaoTokenInfo;
import org.bob.siungongsi.common.dto.ApiResponseCode;
import org.bob.siungongsi.common.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import com.fasterxml.jackson.databind.ObjectMapper;

@RestClientTest(KakaoAuthService.class)
class KakaoAuthServiceTest {

  @Autowired private KakaoAuthService kakaoAuthService;

  @Autowired private MockRestServiceServer mockServer;

  @Autowired private ObjectMapper objectMapper;

  private static final String KAKAO_TOKEN_INFO_URL =
      "https://kapi.kakao.com/v1/user/access_token_info";

  @BeforeEach
  void setUp() {
    mockServer.reset();
  }

  /** getSocialIdFromAccessToken */
  @Test
  @DisplayName("카카오 액세스 토큰으로 소셜 ID를 조회한다")
  void whenGetSocialIdFromAccessToken_thenReturnsSocialId() throws Exception {
    // given
    long socialIdLong = Long.parseLong(TEST_SOCIAL_ID);
    KakaoTokenInfo kakaoResponse = new KakaoTokenInfo(socialIdLong, 21599, 123456);
    String responseBody = objectMapper.writeValueAsString(kakaoResponse);

    mockServer
        .expect(requestTo(KAKAO_TOKEN_INFO_URL))
        .andExpect(method(HttpMethod.GET))
        .andExpect(header("Authorization", TEST_KAKAO_ACCESS_TOKEN))
        .andRespond(
            withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(responseBody));

    // when
    String result = kakaoAuthService.getSocialIdFromAccessToken(TEST_KAKAO_ACCESS_TOKEN);

    // then
    assertThat(result).isEqualTo(TEST_SOCIAL_ID);
    mockServer.verify();
  }

  @Test
  @DisplayName("카카오 API 4xx 에러 발생 시 AUTH_KAKAO_ACCESS_TOKEN_EXPIRED 예외가 발생한다")
  void whenKakaoApi4xxError_thenThrowsAccessTokenExpiredException() {
    // given
    mockServer
        .expect(requestTo(KAKAO_TOKEN_INFO_URL))
        .andExpect(method(HttpMethod.GET))
        .andExpect(header("Authorization", TEST_KAKAO_ACCESS_TOKEN))
        .andRespond(withStatus(HttpStatus.UNAUTHORIZED));

    // when & then
    assertThatThrownBy(() -> kakaoAuthService.getSocialIdFromAccessToken(TEST_KAKAO_ACCESS_TOKEN))
        .isInstanceOf(CustomException.class)
        .hasFieldOrPropertyWithValue("errorCode", ApiResponseCode.AUTH_KAKAO_ACCESS_TOKEN_EXPIRED);

    mockServer.verify();
  }

  @Test
  @DisplayName("카카오 API 5xx 에러 발생 시 AUTH_INTERNAL_SERVER_ERROR 예외가 발생한다")
  void whenKakaoApi5xxError_thenThrowsInternalServerErrorException() {
    // given
    mockServer
        .expect(requestTo(KAKAO_TOKEN_INFO_URL))
        .andExpect(method(HttpMethod.GET))
        .andExpect(header("Authorization", TEST_KAKAO_ACCESS_TOKEN))
        .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

    // when & then
    assertThatThrownBy(() -> kakaoAuthService.getSocialIdFromAccessToken(TEST_KAKAO_ACCESS_TOKEN))
        .isInstanceOf(CustomException.class)
        .hasFieldOrPropertyWithValue("errorCode", ApiResponseCode.AUTH_INTERNAL_SERVER_ERROR);

    mockServer.verify();
  }
}
