package org.bob.siungongsi.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

import org.bob.siungongsi.dto.ApiResponseCode;
import org.bob.siungongsi.exception.CustomException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

import io.sentry.Sentry;
import jakarta.annotation.PostConstruct;

@Profile("dev")
@Service
public class FcmService {

  @Value("${fcm.key}")
  private String firebaseJsonBase64;

  private FirebaseMessaging firebaseMessaging;

  @PostConstruct
  public void initializeFirebase() {
    try {
      if (firebaseJsonBase64 == null || firebaseJsonBase64.isEmpty()) {
        throw new IllegalStateException("no firebase json key");
      }

      byte[] decodedBytes = Base64.getDecoder().decode(firebaseJsonBase64);

      FirebaseOptions options =
          FirebaseOptions.builder()
              .setCredentials(GoogleCredentials.fromStream(new ByteArrayInputStream(decodedBytes)))
              .build();

      if (FirebaseApp.getApps().isEmpty()) {
        FirebaseApp.initializeApp(options);
      }

      this.firebaseMessaging = FirebaseMessaging.getInstance();
    } catch (IOException e) {
      throw new CustomException(
          ApiResponseCode.FCM_INIT_ERROR, ApiResponseCode.FCM_INIT_ERROR.getMessage());
    }
  }

  public void sendNotification(String token, String title, String body) {
    if (token == null || token.isBlank()) {
      return; // 토큰이 없으면 푸시 전송하지 않음
    }

    Message message =
        Message.builder()
            .setToken(token)
            .setNotification(Notification.builder().setTitle(title).setBody(body).build())
            .build();

    try {
      firebaseMessaging.send(message);
    } catch (Exception e) {
      Sentry.captureException(e);
    }
  }
}
