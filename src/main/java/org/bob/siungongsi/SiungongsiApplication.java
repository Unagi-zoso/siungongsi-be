package org.bob.siungongsi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// sentry테스트용. 이후 삭제 예정
import io.sentry.Sentry;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class SiungongsiApplication {

  public static void main(String[] args) {
    SpringApplication.run(SiungongsiApplication.class, args);
  }
}
// sentry테스트용. 이후 삭제 예정
@Profile("dev")
@Component
class SentryStartupTestRunner implements CommandLineRunner {

  @Override
  public void run(String... args) {
    try {
      throw new Exception("🔥 [dev] Spring Boot 앱 시작 시 테스트용 예외입니다!");
    } catch (Exception e) {
      Sentry.captureException(e);
    }
  }
}
