package org.bob.siungongsi.common.config;

import java.util.List;
import java.util.stream.Collectors;

import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class ApiDocsConfig {

  // dev 환경이 https 프로토콜을 사용하게 되며 직접 https 로 URL 을 설정하기 위해 추가했습니다.
  // 프록시로 정보를 다룰 수도 있지만 당장 인프라 쪽을 손보기가 까다로워 애플리케이션 코드쪽에 작업을 했습니다.
  @Value("${swagger.server-url:}")
  private String swaggerServerUrl;

  @Bean
  public OpenAPI openAPI() {
    OpenAPI openAPI =
        new OpenAPI()
            .info(createApiInfo())
            .components(createSecurityComponents())
            .addSecurityItem(createSecurityRequirement());

    if (swaggerServerUrl != null && !swaggerServerUrl.isEmpty()) {
      openAPI.addServersItem(new Server().url(swaggerServerUrl));
    }

    return openAPI;
  }

  // API 문서 정보
  private Info createApiInfo() {
    return new Info().title("시운공시 API").description("시운공시 API 문서입니다.").version("1.0.0");
  }

  // API 보안 설정
  private Components createSecurityComponents() {
    return new Components()
        .addSecuritySchemes(
            AUTHORIZATION,
            new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .in(SecurityScheme.In.HEADER)
                .name(AUTHORIZATION));
  }

  // API 보안 요구사항
  private SecurityRequirement createSecurityRequirement() {
    return new SecurityRequirement().addList(AUTHORIZATION);
  }

  // API 문서에서 Authorization 헤더 제거
  // Authorization 은 스웨거 기능을 통해 전달해야해 그 외 Input 은 제거.
  @Bean
  public OperationCustomizer removeAuthorizationHeader() {
    return (operation, handlerMethod) -> {
      // getParameters()가 null이면 빈 리스트로 처리
      List<Parameter> parameters = operation.getParameters();
      if (parameters == null || parameters.isEmpty()) {
        return operation;
      }

      // Authorization 헤더 제거
      List<Parameter> updatedParameters =
          parameters.stream()
              .filter(param -> !"Authorization".equalsIgnoreCase(param.getName()))
              .collect(Collectors.toList());

      operation.setParameters(updatedParameters);
      return operation;
    };
  }

  private static final String AUTHORIZATION = "Authorization";
}
