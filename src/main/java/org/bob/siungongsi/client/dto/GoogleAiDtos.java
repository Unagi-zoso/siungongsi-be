package org.bob.siungongsi.client.dto;

import java.util.List;

public class GoogleAiDtos {
  public record GoogleAiRequest(List<Content> contents, GenerationConfig generationConfig) {
    public static GoogleAiRequest fromText(String prompt, String text) {
      return new GoogleAiRequest(
          List.of(new Content("user", List.of(new Part(prompt + text)))),
          new GenerationConfig(1, 40, 0.95, 8192, "text/plain"));
    }

    public record Content(String role, List<Part> parts) {}

    public record Part(String text) {}

    public record GenerationConfig(
        int temperature, int topK, double topP, int maxOutputTokens, String responseMimeType) {}
  }

  public record GoogleAiResponse(List<Candidate> candidates) {
    public String getSummary() {
      if (candidates == null || candidates.isEmpty()) {
        return "요약 결과가 없습니다.";
      }

      Candidate firstCandidate = candidates.get(0);
      if (firstCandidate == null
          || firstCandidate.content() == null
          || firstCandidate.content().parts() == null
          || firstCandidate.content().parts().isEmpty()) {
        return "요약 결과가 없습니다.";
      }

      return firstCandidate.content().parts().get(0).text();
    }

    public record Candidate(Content content) {}

    public record Content(List<Part> parts) {}

    public record Part(String text) {}
  }
}
