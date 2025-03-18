package org.bob.siungongsi.service;

import java.util.Optional;

import org.bob.siungongsi.controller.dto.AuthRequest;
import org.bob.siungongsi.domain.UserEntity;
import org.bob.siungongsi.dto.ApiResponseCode;
import org.bob.siungongsi.exception.CustomException;
import org.bob.siungongsi.repository.AuthRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

  private final AuthRepository authRepository;

  public AuthService(AuthRepository authRepository) {
    this.authRepository = authRepository;
  }

  // 회원가입 로직: 사용자가 처음으로 로그인 시 회원을 등록
  public UserEntity authRequest(AuthRequest.RegisterRequest authRequest) {
    Optional<UserEntity> existingUser = authRepository.findBySocialId(authRequest.socialId());

    // 이미 존재하는 사용자가 있다면 예외 처리
    if (existingUser.isPresent()) {
      throw new CustomException(
          ApiResponseCode.AUTH_USER_ALREADY_EXISTS,
          ApiResponseCode.AUTH_USER_ALREADY_EXISTS.getMessage());
    }

    // 새로운 사용자 등록
    UserEntity newUserEntity = new UserEntity(authRequest.socialId(), authRequest.accessToken());
    return authRepository.save(newUserEntity);
  }

  // 로그인 로직: 이미 가입된 사용자가 로그인 시 액세스 토큰 갱신
  public UserEntity login(AuthRequest.LoginRequest authRequest, String socialId) {
    String accessToken = authRequest.accessToken();

    // 액세스 토큰이 없으면 예외 처리
    if (accessToken == null || accessToken.isEmpty()) {
      throw new CustomException(
          ApiResponseCode.AUTH_REQUIRED_AUTHORIZATION,
          ApiResponseCode.AUTH_REQUIRED_AUTHORIZATION.getMessage());
    }

    Optional<UserEntity> user = authRepository.findBySocialId(socialId);

    // 가입되지 않은 사용자의 경우 예외 처리
    if (!user.isPresent()) {
      throw new CustomException(
          ApiResponseCode.AUTH_REQUIRED_AUTHORIZATION,
          ApiResponseCode.AUTH_REQUIRED_AUTHORIZATION.getMessage());
    }

    // 기존 사용자일 경우 액세스 토큰 갱신
    UserEntity userEntity = user.get();
    userEntity.updateAccessToken(accessToken);
    return authRepository.save(userEntity);
  }
}
