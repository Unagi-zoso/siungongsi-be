package org.bob.siungongsi.service;

import java.util.Map;
import java.util.Optional;

import org.bob.siungongsi.domain.UserEntity;
import org.bob.siungongsi.dto.ApiResponseCode;
import org.bob.siungongsi.exception.CustomException;
import org.bob.siungongsi.repository.UserRepository;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
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

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + accessToken);

    HttpEntity<String> entity = new HttpEntity<>(headers);

    try {
      ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

      if (response.getStatusCode() == HttpStatus.OK) {
        Map<String, Object> body = response.getBody();
        if (body != null) {
          return body.get("id").toString(); // 원하는 데이터를 반환
        }
      }
    } catch (Exception e) {
      // 예외 처리 (유효하지 않은 토큰일 경우)
      throw new CustomException(ApiResponseCode.AUTH_REQUIRED_AUTHORIZATION, "유효하지 않은 토큰입니다.");
    }

    return null; // 유효하지 않거나 예외 발생 시 null 반환
  }

  public Long getUserId(String socialId) {
    Optional<UserEntity> user = authRepository.findBySocialId(socialId);
    return user.map(UserEntity::getId).orElse(null);
  }
}
