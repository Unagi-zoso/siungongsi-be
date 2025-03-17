package org.bob.siungongsi.service;

import org.bob.siungongsi.controller.dto.AuthRequest;
import org.bob.siungongsi.domain.UserEntity;

public interface AuthService {
  UserEntity authRequest(AuthRequest authRequest);

  UserEntity login(AuthRequest authRequest);
}
