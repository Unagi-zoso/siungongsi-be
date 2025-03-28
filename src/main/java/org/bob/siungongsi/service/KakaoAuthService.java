package org.bob.siungongsi.service;

import java.util.Map;

import org.bob.siungongsi.dto.ApiResponseCode;
import org.bob.siungongsi.exception.CustomException;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class KakaoAuthService {

  public String getSocialIdFromAccessToken(String accessToken) {
    String url = "https://kapi.kakao.com/v1/user/access_token_info";
    RestClient restClient = RestClient.create();
    return restClient
        .get()
        .uri(url)
        .header("Authorization", accessToken)
        .retrieve()
        .onStatus(
            HttpStatusCode::is4xxClientError,
            ((req, res) -> {
              throw new CustomException(ApiResponseCode.AUTH_ACCESS_TOKEN_EXPIRED, "토큰 만료입니다.");
            }))
        .onStatus(
            HttpStatusCode::is5xxServerError,
            ((req, res) -> {
              throw new CustomException(ApiResponseCode.AUTH_INTERNAL_SERVER_ERROR, "서버 오류입니다.");
            }))
        .body(Map.class)
        .get("id")
        .toString();
  }
}
