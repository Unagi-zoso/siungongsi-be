package org.bob.siungongsi.service;

import org.bob.siungongsi.client.RetryClientHttpRequestInterceptor.RetriesExceededException;
import org.bob.siungongsi.client.clientinterface.GoogleAiClientInterface;
import org.bob.siungongsi.client.dto.GoogleAiDtos.GoogleAiRequest;
import org.bob.siungongsi.client.dto.GoogleAiDtos.GoogleAiResponse;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

@Service
public class GongsiSummarizer {

  private final GoogleAiClientInterface googleAiClientInterface;

  public GongsiSummarizer(GoogleAiClientInterface googleAiClientInterface) {
    this.googleAiClientInterface = googleAiClientInterface;
  }

  public String summarizeText(String text) {
    String prompt = "다음 문서를 요약해 주세요:\n마침표나 문맥이 끊길때마다 개행을 꼭 넣어주세요.\n\n";
    GoogleAiRequest request = GoogleAiRequest.fromText(prompt, Jsoup.parse(text).text());

    try {
      // 🔥 1. 기본 모델 (`gemini-2.0-flash`) 요청
      GoogleAiResponse response = googleAiClientInterface.summarizeWithGemini2Flash(request);
      return response.getSummary();
    } catch (RetriesExceededException e) {
      try {
        // 🔥 2. 대체 모델 (`gemini-2.0-flash-lite`) 요청
        GoogleAiResponse fallbackResponse =
            googleAiClientInterface.summarizeWithGemini2FlashLite(request);
        return fallbackResponse.getSummary();
      } catch (RetriesExceededException ex) {
        throw new RuntimeException("모든 모델 요청 실패", ex);
      }
    }
  }
}
