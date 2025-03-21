package org.bob.siungongsi.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bob.siungongsi.controller.dto.AuthRequest;
import org.bob.siungongsi.controller.dto.TermsResponse;
import org.bob.siungongsi.domain.TermEntity;
import org.bob.siungongsi.domain.UserAgreedTermEntity;
import org.bob.siungongsi.domain.UserEntity;
import org.bob.siungongsi.dto.ApiResponseCode;
import org.bob.siungongsi.exception.CustomException;
import org.bob.siungongsi.repository.TermRepository;
import org.bob.siungongsi.repository.UserAgreedTermRepository;
import org.bob.siungongsi.repository.UserRepository;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class AuthService {

  private final TermRepository termRepository;
  private final UserRepository userRepository;
  private final UserAgreedTermRepository userAgreedTermRepository;
  private final KakaoAuthService kakaoAuthService;

  public AuthService(
      TermRepository termRepository,
      UserRepository userRepository,
      UserAgreedTermRepository userAgreedTermRepository,
      KakaoAuthService kakaoAuthService) {
    this.userRepository = userRepository;
    this.termRepository = termRepository;
    this.userAgreedTermRepository = userAgreedTermRepository;
    this.kakaoAuthService = kakaoAuthService;
  }

  @Transactional
  public void register(AuthRequest.RegisterRequest authRequest) {
    String socialId = kakaoAuthService.validateAccessToken(authRequest.accessToken());

    if (userRepository.existsBySocialId(socialId)) {
      throw new CustomException(ApiResponseCode.AUTH_USER_ALREADY_EXISTS, "이미 가입된 사용자입니다.");
    }

    UserEntity newUserEntity =
        userRepository.save(new UserEntity(socialId, authRequest.accessToken()));

    List<UserAgreedTermEntity> userAgreedTerms =
        validateAndCreateUserAgreedTerms(authRequest.agreedTermIds(), newUserEntity.getId());
    if (!userAgreedTerms.isEmpty()) {
      userAgreedTermRepository.saveAll(userAgreedTerms);
    }
  }

  // 유저가 약관이 필수인 걸 모두 동의해야 가능인 로직을 추가해야 함
  private List<UserAgreedTermEntity> validateAndCreateUserAgreedTerms(
      List<Long> agreedTermIds, Long userId) {
    List<UserAgreedTermEntity> userAgreedTermEntities = new ArrayList<>();

    for (Long termId : agreedTermIds) {
      if (!termRepository.existsById(termId)) {
        throw new CustomException(ApiResponseCode.AUTH_TERMS_ID_NOT_FOUND, "찾을 수 없는 term_id 입니다.");
      }

      if (userAgreedTermRepository.existsByUserIdAndTermId(userId, termId)) {
        throw new CustomException(
            ApiResponseCode.AUTH_USER_AGREED_TERMS_ID_ALREADY_EXISTS, "이미 존재하는 회원 동의 약관 id 입니다.");
      }

      userAgreedTermEntities.add(new UserAgreedTermEntity(userId, termId));
    }

    return userAgreedTermEntities;
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
