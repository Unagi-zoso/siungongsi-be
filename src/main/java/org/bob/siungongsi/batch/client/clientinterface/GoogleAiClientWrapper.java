package org.bob.siungongsi.batch.client.clientinterface;

import org.bob.siungongsi.batch.client.dto.GoogleAiDtos;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

// GoogleAiClientInterface 는 Spring WebClient 기반 인터페이스 선언형 클라이언트로
// AOP 가 프록시 객체에 적용되어 명확하게 로깅되지 않는 문제가 있습니다. 따라서 Wrapper 클래스를 생성하여 AOP 를 적용합니다.
@Profile("batch")
@Component
public class GoogleAiClientWrapper {
  private final GoogleAiClientInterface delegate;

  public GoogleAiClientWrapper(GoogleAiClientInterface googleAiClientInterface) {
    this.delegate = googleAiClientInterface;
  }

  public GoogleAiDtos.GoogleAiResponse summarizeWithGemini2Flash(
      GoogleAiDtos.GoogleAiRequest documentContent) {
    return delegate.summarizeWithGemini2Flash(documentContent);
  }

  public GoogleAiDtos.GoogleAiResponse summarizeWithGemini2FlashLite(
      GoogleAiDtos.GoogleAiRequest documentContent) {
    return delegate.summarizeWithGemini2FlashLite(documentContent);
  }
}
