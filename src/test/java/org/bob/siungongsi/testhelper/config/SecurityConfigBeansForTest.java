package org.bob.siungongsi.testhelper.config;

import static org.bob.siungongsi.fixture.AuthFixture.INVALID_JWT_TOKEN;
import static org.bob.siungongsi.fixture.AuthFixture.TEST_JWT_TOKEN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.bob.siungongsi.api.config.CorsProperties;
import org.bob.siungongsi.api.service.AuthBlackListService;
import org.bob.siungongsi.common.dto.ApiResponseCode;
import org.bob.siungongsi.common.exception.CustomException;
import org.bob.siungongsi.common.security.JwtProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("test")
@Configuration
public class SecurityConfigBeansForTest {

  // 해당 Mock을 임의로 stubbing 할 경우 기존 상태로 초기화 필수
  @Bean
  JwtProvider jwtProvider() {
    JwtProvider mock = mock(JwtProvider.class);
    when(mock.createJwtToken(anyString())).thenReturn(TEST_JWT_TOKEN);
    when(mock.createJwtToken(anyString(), anyLong())).thenReturn(TEST_JWT_TOKEN);

    when(mock.validateJwtToken(anyString())).thenReturn(1L);
    when(mock.validateJwtToken(eq(INVALID_JWT_TOKEN)))
        .thenThrow(new CustomException(ApiResponseCode.AUTH_ACCESS_TOKEN_INVALID_SIGNATURE));

    when(mock.getRemainingExpirationTime(anyString())).thenReturn(3000L);
    return mock;
  }

  @Bean
  CorsProperties corsProperties() {
    return new CorsProperties(List.of("*"));
  }

  // 해당 Mock을 임의로 stubbing 할 경우 기존 상태로 초기화 필수
  @Bean
  AuthBlackListService authBlackListService() {
    AuthBlackListService mock = mock(AuthBlackListService.class);
    doNothing().when(mock).setBlackList(anyString(), any(), eq(300000L));

    when(mock.hasKeyBlackList(anyString())).thenReturn(false);

    when(mock.deleteBlackList(anyString())).thenReturn(true);
    return mock;
  }
}
