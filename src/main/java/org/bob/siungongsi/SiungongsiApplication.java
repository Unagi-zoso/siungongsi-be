package org.bob.siungongsi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.sentry.Sentry;

@SpringBootApplication
public class SiungongsiApplication {

  public static void main(String[] args) {
    SpringApplication.run(SiungongsiApplication.class, args);
  }
}
