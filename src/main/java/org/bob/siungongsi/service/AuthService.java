package org.bob.siungongsi.service;

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
import org.bob.siungongsi.repository.NotificationRepository;
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
  private final NotificationRepository notificationRepository;

  public AuthService(
      TermRepository termRepository,
      UserRepository userRepository,
      UserAgreedTermRepository userAgreedTermRepository,
      KakaoAuthService kakaoAuthService,
      NotificationRepository notificationRepository) {
    this.userRepository = userRepository;
    this.termRepository = termRepository;
    this.userAgreedTermRepository = userAgreedTermRepository;
    this.kakaoAuthService = kakaoAuthService;
    this.notificationRepository = notificationRepository;
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

  private List<UserAgreedTermEntity> validateAndCreateUserAgreedTerms(
      List<Long> agreedTermIds, Long userId) {

    validateRequiredTerms(agreedTermIds);

    validateTermIds(agreedTermIds, userId);

    return agreedTermIds.stream().map(termId -> new UserAgreedTermEntity(userId, termId)).toList();
  }

  private void validateRequiredTerms(List<Long> agreedTermIds) {
    List<Long> requiredTermIds = termRepository.findIdsByRequiredFlag();

    if (!agreedTermIds.containsAll(requiredTermIds)) {
      throw new CustomException(ApiResponseCode.AUTH_REQUIRED_TERMS_NOT_AGREED, "필수 약관에 동의해야 합니다.");
    }
  }

  private void validateTermIds(List<Long> agreedTermIds, Long userId) {
    for (Long termId : agreedTermIds) {
      if (!termRepository.existsById(termId)) {
        throw new CustomException(ApiResponseCode.AUTH_TERMS_ID_NOT_FOUND, "찾을 수 없는 term_id 입니다.");
      }

      if (userAgreedTermRepository.existsByUserIdAndTermId(userId, termId)) {
        throw new CustomException(
            ApiResponseCode.AUTH_USER_AGREED_TERMS_ID_ALREADY_EXISTS, "이미 존재하는 회원 동의 약관 id 입니다.");
      }
    }
  }

  public Boolean login(String accessToken, String socialId) {

    Optional<UserEntity> user = userRepository.findBySocialId(socialId);

    if (!user.isPresent()) {
      return false;
    }

    // 기존 사용자일 경우 액세스 토큰 갱신
    UserEntity userEntity = user.get();
    userEntity.updateAccessToken(accessToken);
    userRepository.save(userEntity);
    return true;
  }

  @Transactional
  public void withdrawUser(String accessToken) {
    // 액세스 토큰이 없으면 예외 처리
    if (accessToken == null || accessToken.isEmpty()) {
      throw new CustomException(
          ApiResponseCode.AUTH_REQUIRED_AUTHORIZATION,
          ApiResponseCode.AUTH_REQUIRED_AUTHORIZATION.getMessage());
    }

    String socialId = kakaoAuthService.validateAccessToken(accessToken);

    Optional<UserEntity> userOpt = userRepository.findBySocialId(socialId);

    // 유효하지 않은 소셜 ID인 경우 예외 처리
    if (!userOpt.isPresent()) {
      throw new CustomException(
          ApiResponseCode.AUTH_USER_NOT_FOUND, ApiResponseCode.AUTH_USER_NOT_FOUND.getMessage());
    }

    UserEntity user = userOpt.get();
    Long userId = user.getId();

    // 회원의 알림 구독 정보 삭제
    notificationRepository.deleteAllByUserId(userId);

    // 회원의 약관 동의 정보 삭제
    userAgreedTermRepository.deleteAllByUserId(userId);

    // 회원 정보 삭제
    userRepository.delete(user);
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
