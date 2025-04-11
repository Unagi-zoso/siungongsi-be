package org.bob.siungongsi.batch.client.clientinterface;

import org.bob.siungongsi.batch.client.dto.OpenDartDtos.GongsiListResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

// OpenDartClientInterface 는 Spring WebClient 기반 인터페이스 선언형 클라이언트로
// AOP 가 프록시 객체에 적용되어 명확하게 로깅되지 않는 문제가 있습니다. 따라서 Wrapper 클래스를 생성하여 AOP 를 적용합니다.
@Profile("batch")
@Component
public class OpenDartClientWrapper {

  private final OpenDartClientInterface delegate;

  public OpenDartClientWrapper(OpenDartClientInterface openDartClientInterface) {
    this.delegate = openDartClientInterface;
  }

  public GongsiListResponse getOpenDartList(String bgnDe, String endDe, int pageNo, int pageCount) {
    return delegate.getOpenDartList(bgnDe, endDe, pageNo, pageCount);
  }

  public byte[] getOpenDartDocument(String rceptNo) {
    return delegate.getOpenDartDocument(rceptNo);
  }
}
