package org.bob.siungongsi.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

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

  public void sendNotification(String token, String title, String body, String url) {
    if (token == null || token.isBlank()) {
      return;
    }

    Map<String, String> data = new HashMap<>();
    data.put("title", title);
    data.put("body", body);
    data.put("url", url);

    Message message = Message.builder().setToken(token).putAllData(data).build();

    try {
      firebaseMessaging.send(message);
    } catch (Exception e) {
      Sentry.captureException(e);
    }
  }
}
