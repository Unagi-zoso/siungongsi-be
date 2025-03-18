package org.bob.siungongsi.service;

import java.util.Map;
import java.util.Optional;

import org.bob.siungongsi.domain.UserEntity;
import org.bob.siungongsi.repository.AuthRepository;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class KakaoAuthService {

  private final RestTemplate restTemplate = new RestTemplate();

  private final AuthRepository authRepository;

  public KakaoAuthService(AuthRepository authRepository) {
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
        // 응답에서 'id'와 'status' 추출
        Map<String, Object> body = response.getBody();
        if (body != null) {
          Object id = body.get("id");
          Object expiresIn = body.get("expires_in");

          // id와 expires_in 값을 필요한 곳에 사용할 수 있도록 반환하거나 로그로 출력 가능
          System.out.println("ID: " + id);
          System.out.println("Expires In: " + expiresIn);

          return id.toString(); // 원하는 데이터를 반환
        }
      }
    } catch (Exception e) {
      // 예외 처리 (유효하지 않은 토큰일 경우)
      System.out.println("Error: " + e.getMessage());
    }

    return null; // 유효하지 않거나 예외 발생 시 null 반환
  }

  public Long getUserId(String socialId) {
    Optional<UserEntity> user = authRepository.findBySocialId(socialId);
    return user.map(UserEntity::getId).orElse(null);
  }
}
