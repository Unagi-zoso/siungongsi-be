package org.bob.siungongsi.api.service;

import java.util.List;
import java.util.stream.Collectors;

import org.bob.siungongsi.api.controller.dto.AuthRequest;
import org.bob.siungongsi.api.controller.dto.AuthResponse;
import org.bob.siungongsi.api.controller.dto.TermsResponse;
import org.bob.siungongsi.common.domain.TermEntity;
import org.bob.siungongsi.common.domain.UserAgreedTermEntity;
import org.bob.siungongsi.common.domain.UserEntity;
import org.bob.siungongsi.common.dto.ApiResponseCode;
import org.bob.siungongsi.common.exception.CustomException;
import org.bob.siungongsi.common.repository.NotificationRepository;
import org.bob.siungongsi.common.repository.TermRepository;
import org.bob.siungongsi.common.repository.UserAgreedTermRepository;
import org.bob.siungongsi.common.repository.UserRepository;
import org.bob.siungongsi.common.security.JwtProvider;
import org.bob.siungongsi.common.util.RedisUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class AuthService {

  private final TermRepository termRepository;
  private final UserRepository userRepository;
  private final UserAgreedTermRepository userAgreedTermRepository;
  private final KakaoAuthService kakaoAuthService;
  private final NotificationRepository notificationRepository;
  private final JwtProvider jwtProvider;
  private final RedisUtils redisUtils;

  public AuthService(
      TermRepository termRepository,
      UserRepository userRepository,
      UserAgreedTermRepository userAgreedTermRepository,
      KakaoAuthService kakaoAuthService,
      NotificationRepository notificationRepository,
      JwtProvider jwtProvider,
      RedisUtils redisUtils) {
    this.userRepository = userRepository;
    this.termRepository = termRepository;
    this.userAgreedTermRepository = userAgreedTermRepository;
    this.kakaoAuthService = kakaoAuthService;
    this.notificationRepository = notificationRepository;
    this.jwtProvider = jwtProvider;
    this.redisUtils = redisUtils;
  }

  @Transactional
  public String register(AuthRequest.RegisterRequest authRequest, String accessToken) {

    if (accessToken == null || accessToken.isEmpty()) {
      throw new CustomException(ApiResponseCode.AUTH_REQUIRED_AUTHORIZATION);
    }

    String socialId = kakaoAuthService.getSocialIdFromAccessToken(accessToken);

    if (userRepository.existsBySocialId(socialId)) {
      throw new CustomException(ApiResponseCode.AUTH_USER_ALREADY_EXISTS);
    }

    Long userId = userRepository.save(new UserEntity(socialId, accessToken.substring(7))).getId();

    List<UserAgreedTermEntity> userAgreedTerms =
        validateAndCreateUserAgreedTerms(authRequest.agreedTermIds(), userId);
    if (!userAgreedTerms.isEmpty()) {
      userAgreedTermRepository.saveAll(userAgreedTerms);
    }

    return createJwt(userId.toString());
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
      throw new CustomException(ApiResponseCode.AUTH_REQUIRED_TERMS_NOT_AGREED);
    }
  }

  private void validateTermIds(List<Long> agreedTermIds, Long userId) {
    for (Long termId : agreedTermIds) {
      if (!termRepository.existsById(termId)) {
        throw new CustomException(ApiResponseCode.AUTH_TERMS_ID_NOT_FOUND);
      }

      if (userAgreedTermRepository.existsByUserIdAndTermId(userId, termId)) {
        throw new CustomException(ApiResponseCode.AUTH_USER_AGREED_TERMS_ID_ALREADY_EXISTS);
      }
    }
  }

  public AuthResponse.LoginSuccessResponse login(String accessToken) {
    String socialId = kakaoAuthService.getSocialIdFromAccessToken(accessToken);

    UserEntity user = userRepository.findBySocialId(socialId).orElse(null);

    if (user == null) {
      return AuthResponse.LoginSuccessResponse.of(null, false);
    }

    user.updateAccessToken(accessToken.substring(7));
    userRepository.save(user);
    String jwt = jwtProvider.createJwtToken(user.getId().toString());
    return AuthResponse.LoginSuccessResponse.of(jwt, true);
  }

  public String createJwt(String userId) {
    return jwtProvider.createJwtToken(userId);
  }

  public void logout(String accessToken) {
    redisUtils.setBlackList(
        "blacklist:" + accessToken, "logout", jwtProvider.getRemainingExpirationTime(accessToken));
  }

  @Transactional
  public void withdrawUser() {

    Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    if (!userRepository.existsById(userId)) {
      throw new CustomException(ApiResponseCode.AUTH_USER_NOT_FOUND);
    }

    // 회원의 알림 구독 정보 삭제
    notificationRepository.deleteAllByUserId(userId);

    // 회원의 약관 동의 정보 삭제
    userAgreedTermRepository.deleteAllByUserId(userId);

    // 회원 정보 삭제
    userRepository.deleteById(userId);
  }

  // 약관 정보 조회 로직
  public List<TermsResponse> getTerms() {
    List<TermEntity> terms = termRepository.findAll();

    if (terms.isEmpty()) {
      throw new CustomException(ApiResponseCode.AUTH_TERMS_NOT_FOUND);
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
