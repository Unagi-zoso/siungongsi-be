package org.bob.siungongsi.controller.dto;

public class UserResponse {

  public record NotificationStatusResponse(Long userId, Boolean notificationFlag) {
    public static NotificationStatusResponse of(Long userId, Boolean notificationFlag) {
      return new NotificationStatusResponse(userId, notificationFlag);
    }
  }
}
