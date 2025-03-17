package org.bob.siungongsi.service;

import java.util.Optional;

import org.bob.siungongsi.controller.dto.AuthRequest;
import org.bob.siungongsi.domain.UserEntity;
import org.bob.siungongsi.dto.ApiResponseCode;
import org.bob.siungongsi.exception.CustomException;
import org.bob.siungongsi.repository.AuthRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

  private final AuthRepository authRepository;

  public AuthServiceImpl(AuthRepository authRepository) {
    this.authRepository = authRepository;
  }

  @Override
  public UserEntity authRequest(AuthRequest authRequest) {
    Optional<UserEntity> existingUser = authRepository.findBySocialId(authRequest.socialId());

    if (existingUser.isPresent()) {
      throw new CustomException(
          ApiResponseCode.AUTH_USER_ALREADY_EXISTS,
          ApiResponseCode.AUTH_USER_ALREADY_EXISTS.getMessage());
    }

    UserEntity userEntity = new UserEntity(authRequest.socialId(), authRequest.accessToken());
    return authRepository.save(userEntity);
  }
}
