package org.bob.siungongsi.common.config;

import java.util.List;
import java.util.stream.Collectors;

import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class ApiDocsConfig {

  @Bean
  public OpenAPI openAPI() {
    return new OpenAPI()
        .info(createApiInfo())
        .components(createSecurityComponents())
        .addSecurityItem(createSecurityRequirement());
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
