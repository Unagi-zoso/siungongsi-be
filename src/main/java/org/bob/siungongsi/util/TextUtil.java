package org.bob.siungongsi.util;

public class TextUtil {
  public static String removeMarkdown(String text) {
    // 굵은 글씨 및 기울임 제거
    text = text.replaceAll("(\\*\\*|__|\\*|_)", "");

    // 헤더(#), 목록(-, *, •) 제거
    text = text.replaceAll("(?m)^#{1,6}\\s*", ""); // 헤더 (#, ## 등)
    text = text.replaceAll("(?m)^[-*•]\\s*", ""); // 목록 (-, *, •)

    // 링크 제거 [텍스트](URL) -> 텍스트만 남김
    text = text.replaceAll("\\[([^\\]]+)\\]\\([^\\)]+\\)", "$1");

    return text.trim();
  }
}
