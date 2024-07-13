package com.andersen.products_app.unit_tests.service.impl;

import static com.andersen.utils.Constants.EMAIL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.andersen.products_app.service.impl.JwtService;
import java.security.Key;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

class JwtServiceImplTest {
  private final JwtService jwtService = new JwtService();

  @Mock
  private Key key;

  @BeforeEach
  void setUp() {
    var secretKey = "testSecretKey12345678901234567890123456789012";
    ReflectionTestUtils.setField(jwtService, "secretKey", secretKey);
    var expirationTime = 1000L * 60 * 60;
    ReflectionTestUtils.setField(jwtService, "expirationTime", expirationTime);
  }

  @Test
  void whenGenerateToken_thenReturnToken() {
    var token = jwtService.generateToken(EMAIL);

    assertNotNull(token);
    assertEquals(EMAIL, jwtService.extractEmail(token));
  }

  @Test
  void whenValidateToken_thenTokenValid() {
    var token = jwtService.generateToken(EMAIL);

    assertTrue(jwtService.validateToken(token));
  }

  @Test
  void whenValidateToken_thenTokenExpired() {
    ReflectionTestUtils.setField(jwtService, "expirationTime", -100L);

    var token = jwtService.generateToken(EMAIL);

    assertFalse(jwtService.validateToken(token));
  }
}
