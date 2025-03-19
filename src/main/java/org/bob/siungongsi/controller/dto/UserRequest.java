package org.bob.siungongsi.controller.dto;

public class UserRequest {
  public record UserNotificationRequest(Boolean notificationFlag, String pushToken) {
    public static UserNotificationRequest of(Boolean notificationFlag, String pushToken) {
      return new UserNotificationRequest(notificationFlag, pushToken);
    }
  }
}
