package org.bob.siungongsi.service;

import java.util.Map;
import java.util.Optional;

import org.bob.siungongsi.domain.UserEntity;
import org.bob.siungongsi.dto.ApiResponseCode;
import org.bob.siungongsi.exception.CustomException;
import org.bob.siungongsi.repository.UserRepository;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

@Service
public class KakaoAuthService {

  private final RestTemplate restTemplate = new RestTemplate();

  private final UserRepository authRepository;

  public KakaoAuthService(UserRepository authRepository) {
    this.authRepository = authRepository;
  }

  public String validateAccessToken(String accessToken) {
    String url = "https://kapi.kakao.com/v1/user/access_token_info";
    RestClient restClient = RestClient.create();
    return restClient
        .get()
        .uri(url)
        .header("Authorization", "Bearer " + accessToken)
        .retrieve()
        .onStatus(
            HttpStatusCode::is4xxClientError,
            ((req, res) -> {
              throw new CustomException(ApiResponseCode.AUTH_ACCESS_TOKEN_EXPIRED, "토큰 만료입니다.");
            }))
        .body(Map.class)
        .get("id")
        .toString();
  }

  public Long getUserId(String socialId) {
    Optional<UserEntity> user = authRepository.findBySocialId(socialId);
    return user.map(UserEntity::getId).orElse(null);
  }
}
