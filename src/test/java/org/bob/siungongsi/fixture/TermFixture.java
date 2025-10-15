package org.bob.siungongsi.fixture;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.stream.IntStream;

import org.bob.siungongsi.api.controller.dto.TermsResponse;
import org.bob.siungongsi.common.domain.TermEntity;

public class TermFixture {

  // 약관 ID 상수
  public static final Long TERM_ID_1 = 1L;
  public static final Long TERM_ID_2 = 2L;
  public static final Long TERM_ID_3 = 3L;

  // 약관 제목 상수
  public static final String REQUIRED_TERM_TITLE_1 = "서비스 이용약관";
  public static final String REQUIRED_TERM_TITLE_2 = "개인정보 처리방침";
  public static final String OPTIONAL_TERM_TITLE = "마케팅 수신 동의";

  // 약관 내용 상수
  public static final String TERM_CONTENT_1 = "서비스 이용약관 내용입니다.";
  public static final String TERM_CONTENT_2 = "개인정보 처리방침 내용입니다.";
  public static final String TERM_CONTENT_3 = "마케팅 수신 동의 내용입니다.";

  // 약관 필수 여부 플래그
  public static final Integer REQUIRED_FLAG = 1;
  public static final Integer OPTIONAL_FLAG = 0;

  /**
   * TermEntity는 protected 생성자만 제공하고 setter 메서드가 없어 일반적인 방법으로 인스턴스 생성이 불가능합니다. 따라서 Mock 객체를 생성하고 필요한
   * 필드값을 stub하여 반환합니다.
   */
  public static TermEntity createStubbedTermEntity(
      Long id, String title, String content, Integer requiredFlag) {
    TermEntity term = mock(TermEntity.class);
    when(term.getId()).thenReturn(id);
    when(term.getTitle()).thenReturn(title);
    when(term.getContent()).thenReturn(content);
    when(term.getRequiredFlag()).thenReturn(requiredFlag);
    return term;
  }

  // 단건 생성 편의 메서드
  public static TermEntity createRequiredTermEntity1() {
    return createStubbedTermEntity(TERM_ID_1, REQUIRED_TERM_TITLE_1, TERM_CONTENT_1, REQUIRED_FLAG);
  }

  public static TermEntity createRequiredTermEntity2() {
    return createStubbedTermEntity(TERM_ID_2, REQUIRED_TERM_TITLE_2, TERM_CONTENT_2, REQUIRED_FLAG);
  }

  public static TermEntity createOptionalTermEntity() {
    return createStubbedTermEntity(TERM_ID_3, OPTIONAL_TERM_TITLE, TERM_CONTENT_3, OPTIONAL_FLAG);
  }

  // 다건 생성 편의 메서드
  public static List<TermEntity> createAllTermEntities() {
    return List.of(
        createRequiredTermEntity1(), createRequiredTermEntity2(), createOptionalTermEntity());
  }

  public static List<TermEntity> createRequiredTermEntities() {
    return List.of(createRequiredTermEntity1(), createRequiredTermEntity2());
  }

  public static List<TermEntity> createTermEntities(int size) {
    return IntStream.range(0, size)
        .mapToObj(
            i ->
                createStubbedTermEntity(
                    (long) (i + 1), REQUIRED_TERM_TITLE_1 + i, TERM_CONTENT_1 + i, REQUIRED_FLAG))
        .toList();
  }

  // TermsResponse Builder
  public static class TermsResponseBuilder {
    private Long termsId = TERM_ID_1;
    private String termsTitle = REQUIRED_TERM_TITLE_1 + " (필수)";
    private String termsContent = TERM_CONTENT_1;

    public TermsResponseBuilder termsId(Long termsId) {
      this.termsId = termsId;
      return this;
    }

    public TermsResponseBuilder termsTitle(String termsTitle) {
      this.termsTitle = termsTitle;
      return this;
    }

    public TermsResponseBuilder termsContent(String termsContent) {
      this.termsContent = termsContent;
      return this;
    }

    public TermsResponse build() {
      return TermsResponse.of(termsId, termsTitle, termsContent);
    }
  }

  public static TermsResponseBuilder termsResponse() {
    return new TermsResponseBuilder();
  }

  // TermsResponse 편의 메서드
  public static TermsResponse createRequiredTermsResponse1() {
    return termsResponse()
        .termsId(TERM_ID_1)
        .termsTitle(REQUIRED_TERM_TITLE_1 + " (필수)")
        .termsContent(TERM_CONTENT_1)
        .build();
  }

  public static TermsResponse createRequiredTermsResponse2() {
    return termsResponse()
        .termsId(TERM_ID_2)
        .termsTitle(REQUIRED_TERM_TITLE_2 + " (필수)")
        .termsContent(TERM_CONTENT_2)
        .build();
  }

  public static List<TermsResponse> createAllTermsResponses() {
    return List.of(createRequiredTermsResponse1(), createRequiredTermsResponse2());
  }
}
