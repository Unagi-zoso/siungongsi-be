package org.bob.siungongsi.domain;

public enum PushStatus {
  PENDING, // 아직 푸시 요청을 생성하지 않음
  SENT, // 푸시 요청이 생성되었고 전송 완료됨
  FAILED // 푸시가 실패하여 더 이상 처리하지 않음
}
