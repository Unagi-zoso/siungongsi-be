package org.bob.siungongsi.api.controller;

import org.bob.siungongsi.api.controller.spec.DevSupportControllerSpec;
import org.bob.siungongsi.api.service.UserService;
import org.bob.siungongsi.common.dto.ApiResponseWrapper;
import org.bob.siungongsi.common.security.JwtProvider;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Profile({"dev", "local"})
@RestController
@RequestMapping("/support")
public class DevSupportController implements DevSupportControllerSpec {

  private final UserService userService;
  private final JwtProvider jwtProvider;

  public DevSupportController(UserService userService, JwtProvider jwtProvider) {
    this.userService = userService;
    this.jwtProvider = jwtProvider;
  }

  @GetMapping("/user")
  @Override
  public ResponseEntity<ApiResponseWrapper<?>> getUser() {
    ApiResponseWrapper<?> response =
        new ApiResponseWrapper<>(12341243, "토큰 획득용 유저 정보", userService.getUser());
    return ResponseEntity.ok(response);
  }

  @PostMapping("/token")
  @Override
  public ResponseEntity<ApiResponseWrapper<?>> getToken(
      @RequestParam("userId") String userId,
      @RequestParam(value = "expirationTime", required = false) Long expirationTime) {

    String jwtToken;
    if (expirationTime != null) {
      jwtToken = jwtProvider.createJwtToken(userId, expirationTime);
    } else {
      jwtToken = jwtProvider.createJwtToken(userId);
    }

    ApiResponseWrapper<?> response = new ApiResponseWrapper<>(12341243, "토큰 획득", jwtToken);
    return ResponseEntity.ok(response);
  }
}
