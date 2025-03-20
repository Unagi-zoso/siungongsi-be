package org.bob.siungongsi.service;

import java.util.List;

import org.bob.siungongsi.domain.GongsiEntity;
import org.bob.siungongsi.domain.NotiHistoryEntity;
import org.bob.siungongsi.domain.UserEntity;
import org.bob.siungongsi.repository.NotificationRepository;
import org.bob.siungongsi.repository.UserRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("dev")
@Service
public class PushNotiService {

  private final FcmService fcmService;
  private final NotificationRepository notificationRepository;
  private final UserRepository userRepository;

  public PushNotiService(
      FcmService fcmService,
      NotificationRepository notificationRepository,
      UserRepository userRepository) {
    this.fcmService = fcmService;
    this.notificationRepository = notificationRepository;
    this.userRepository = userRepository;
  }

  public boolean sendPushNotification(GongsiEntity gongsi) {
    List<NotiHistoryEntity> notiHistoryEntities =
        notificationRepository.findByCompanyId(gongsi.getCompany().getId());
    List<UserEntity> subscribers =
        userRepository.findAllById(
            notiHistoryEntities.stream().map(NotiHistoryEntity::getUserId).toList());
    for (UserEntity user : subscribers) {
      if (!sendMessage(user.getPushTokenId(), gongsi)) {
        return false;
      }
    }
    return true;
  }

  private boolean sendMessage(String token, GongsiEntity gongsi) {
    String title = gongsi.getCompany().getCompanyName() + "기업의 새로운 공시가 나왔습니다.";
    String body = "http://siungongsi.site/gongsi/" + gongsi.getId();
    fcmService.sendNotification(token, title, body);
    return true;
  }
}
