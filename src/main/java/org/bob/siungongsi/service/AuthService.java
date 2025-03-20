package org.bob.siungongsi.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bob.siungongsi.controller.dto.AuthRequest;
import org.bob.siungongsi.controller.dto.TermsResponse;
import org.bob.siungongsi.domain.TermEntity;
import org.bob.siungongsi.domain.UserEntity;
import org.bob.siungongsi.dto.ApiResponseCode;
import org.bob.siungongsi.exception.CustomException;
import org.bob.siungongsi.repository.TermRepository;
import org.bob.siungongsi.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

  private final TermRepository termRepository;
  private final UserRepository userRepository;

  public AuthService(TermRepository termRepository, UserRepository userRepository) {
    this.userRepository = userRepository;
    this.termRepository = termRepository;
  }

  public UserEntity register(AuthRequest.RegisterRequest authRequest) {
    Optional<UserEntity> existingUser = userRepository.findBySocialId(authRequest.socialId());

    if (existingUser.isPresent()) {
      throw new CustomException(ApiResponseCode.AUTH_USER_ALREADY_EXISTS, "이미 가입된 사용자입니다. ");
    }

    UserEntity newUserEntity = new UserEntity(authRequest.socialId(), authRequest.accessToken());
    return userRepository.save(newUserEntity);
  }

  public UserEntity login(AuthRequest.LoginRequest authRequest, String socialId) {
    String accessToken = authRequest.accessToken();

    Optional<UserEntity> user = userRepository.findBySocialId(socialId);

    if (!user.isPresent()) {
      throw new CustomException(ApiResponseCode.AUTH_USER_NOT_FOUND, "회원을 찾을 수 없습니다.");
    }

    // 기존 사용자일 경우 액세스 토큰 갱신
    UserEntity userEntity = user.get();
    userEntity.updateAccessToken(accessToken);
    return userRepository.save(userEntity);
  }

  // 회원탈퇴 로직: 인증된 사용자의 계정을 삭제
  public void withdrawUser(String accessToken) {
    // 액세스 토큰이 없으면 예외 처리
    if (accessToken == null || accessToken.isEmpty()) {
      throw new CustomException(
          ApiResponseCode.AUTH_REQUIRED_AUTHORIZATION,
          ApiResponseCode.AUTH_REQUIRED_AUTHORIZATION.getMessage());
    }

    Optional<UserEntity> user = userRepository.findByAccessToken(accessToken);

    // 유효하지 않은 액세스 토큰인 경우 예외 처리
    if (!user.isPresent()) {
      throw new CustomException(
          ApiResponseCode.AUTH_USER_NOT_FOUND, ApiResponseCode.AUTH_USER_NOT_FOUND.getMessage());
    }

    // 사용자 계정 삭제
    userRepository.delete(user.get());
  }

  // 약관 정보 조회 로직
  public List<TermsResponse> getTerms() {
    List<TermEntity> terms = termRepository.findAll();

    if (terms.isEmpty()) {
      throw new CustomException(
          ApiResponseCode.AUTH_TERMS_NOT_FOUND, ApiResponseCode.AUTH_TERMS_NOT_FOUND.getMessage());
    }

    return terms.stream()
        .map(
            term -> {
              String titleWithRequiredFlag =
                  term.getRequiredFlag() == 1
                      ? term.getTitle() + " (필수)"
                      : term.getTitle() + " (선택)";
              return TermsResponse.of(term.getId(), titleWithRequiredFlag, term.getContent());
            })
        .collect(Collectors.toList());
  }
}
