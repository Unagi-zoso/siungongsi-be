package org.bob.siungongsi.api.service;

import java.util.Objects;

import org.bob.siungongsi.common.dto.ApiResponseCode;
import org.bob.siungongsi.common.exception.CustomException;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.annotation.JsonProperty;

@Service
public class KakaoAuthService {

  private final RestClient restClient;

  public KakaoAuthService(RestClient.Builder restClientBuilder) {
    this.restClient = restClientBuilder.build();
  }

  public String getSocialIdFromAccessToken(String accessToken) {
    String url = "https://kapi.kakao.com/v1/user/access_token_info";
    KakaoTokenInfo result =
        restClient
            .get()
            .uri(url)
            .header("Authorization", accessToken)
            .retrieve()
            .onStatus(
                HttpStatusCode::is4xxClientError,
                ((req, res) -> {
                  throw new CustomException(ApiResponseCode.AUTH_KAKAO_ACCESS_TOKEN_EXPIRED);
                }))
            .onStatus(
                HttpStatusCode::is5xxServerError,
                ((req, res) -> {
                  throw new CustomException(ApiResponseCode.AUTH_INTERNAL_SERVER_ERROR);
                }))
            .body(KakaoTokenInfo.class);

    return Objects.requireNonNull(result, "Kakao API returned null response").id().toString();
  }

  record KakaoTokenInfo(
      Long id,
      @JsonProperty("expires_in") Integer expiresIn,
      @JsonProperty("app_id") Integer appId) {}
}
