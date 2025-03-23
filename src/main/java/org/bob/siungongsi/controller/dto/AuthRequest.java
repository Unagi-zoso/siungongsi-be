package org.bob.siungongsi.controller.dto;

import java.util.List;

public class AuthRequest {
  public record RegisterRequest(String accessToken, List<Long> agreedTermIds) {}

  public record LoginRequest(String accessToken) {}
}
