package org.bob.siungongsi.api.controller.dto;

public class UserResponse {

  public record NotificationStatusResponse(Long userId, Boolean notificationFlag) {
    public static NotificationStatusResponse of(Long userId, Boolean notificationFlag) {
      return new NotificationStatusResponse(userId, notificationFlag);
    }
  }
}
