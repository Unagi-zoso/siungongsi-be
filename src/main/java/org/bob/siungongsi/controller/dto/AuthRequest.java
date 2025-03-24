package org.bob.siungongsi.controller.dto;

import java.util.List;

public class AuthRequest {
  public record RegisterRequest(List<Long> agreedTermIds) {}
}
