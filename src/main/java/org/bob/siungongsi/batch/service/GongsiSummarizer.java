package org.bob.siungongsi.batch.service;

import static org.bob.siungongsi.batch.util.TextUtil.removeMarkdown;

import org.bob.siungongsi.batch.client.clientinterface.GoogleAiClientWrapper;
import org.bob.siungongsi.batch.client.dto.GoogleAiDtos.GoogleAiRequest;
import org.bob.siungongsi.batch.client.dto.GoogleAiDtos.GoogleAiResponse;
import org.bob.siungongsi.common.client.RetryClientHttpRequestInterceptor.RetriesExceededException;
import org.jsoup.Jsoup;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("batch")
@Service
public class GongsiSummarizer {

  private final GoogleAiClientWrapper googleAiClient;

  private static final String DEFAULT_SUMMARY_PROMPT =
      "다음 문서를 요약해 주세요.\n"
          + "반드시 일반 텍스트로만 작성하고, 표, 강조 표시(예: **, __, *), 마크다운 문법(#, -, • 등)을 절대 사용하지 마세요.\n"
          + "모든 문서는 동일한 형식으로 정리해야 합니다.\n"
          + "주요 내용을 개요, 세부 정보, 일정 순서로 정리해 주세요.\n"
          + "문장은 간결하게 작성하고, 문맥이 끊길 때마다 개행해 주세요.\n"
          + "항목 구분은 기호 없이 문장으로 표현하고, 제목은 따로 표시하지 마세요.\n"
          + "각 항목은 새로운 줄에서 시작해야 합니다.\n";

  public GongsiSummarizer(GoogleAiClientWrapper googleAiClient) {
    this.googleAiClient = googleAiClient;
  }

  public String summarizeText(String text, String prompt) {

    GoogleAiRequest request = GoogleAiRequest.fromText(prompt, Jsoup.parse(text).text());

    try {
      // 🔥 1. 기본 모델 (`gemini-2.0-flash`) 요청
      GoogleAiResponse response = googleAiClient.summarizeWithGemini2Flash(request);
      return removeMarkdown(response.getSummary());
    } catch (RetriesExceededException e) {
      try {
        // 🔥 2. 대체 모델 (`gemini-2.0-flash-lite`) 요청
        GoogleAiResponse fallbackResponse = googleAiClient.summarizeWithGemini2FlashLite(request);
        return removeMarkdown(fallbackResponse.getSummary());
      } catch (RetriesExceededException ex) {
        throw new RuntimeException("모든 모델 요청 실패", ex);
      }
    }
  }

  public String summarizeText(String text) {
    return summarizeText(text, DEFAULT_SUMMARY_PROMPT);
  }
}
