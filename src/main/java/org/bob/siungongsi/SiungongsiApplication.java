package org.bob.siungongsi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SiungongsiApplication {

  public static void main(String[] args) {
    SpringApplication.run(SiungongsiApplication.class, args);
  }
}

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
