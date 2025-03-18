package org.bob.siungongsi.controller.dto;

public class AuthRequest {
  public record RegisterRequest(String accessToken, String socialId) {}

  public record LoginRequest(String accessToken) {}
}
