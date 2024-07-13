package com.andersen.products_app.unit_tests.controller;

import static com.andersen.utils.Constants.EMAIL;
import static com.andersen.utils.Constants.FIRST_NAME;
import static com.andersen.utils.Constants.LAST_NAME;
import static com.andersen.utils.Constants.PASSWORD;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersen.products_app.config.security.JwtAuthFilter;
import com.andersen.products_app.controller.AuthController;
import com.andersen.products_app.model.request.LoginRequest;
import com.andersen.products_app.model.request.RegisterUserRequest;
import com.andersen.products_app.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@WebMvcTest(AuthController.class)
class AuthControllerTest {
  @Autowired
  private WebApplicationContext webApplicationContext;
  @MockBean
  private AuthService authService;
  @MockBean
  private JwtAuthFilter jwtAuthFilter;
  @MockBean
  private UserDetailsService userDetailsService;
  @Spy
  private ObjectMapper objectMapper;
  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
  }

  @Test
  void whenRegisterUser_thenCreatedReturned() throws Exception {
    var request =
        new RegisterUserRequest(FIRST_NAME, LAST_NAME, EMAIL, PASSWORD);

    mockMvc.perform(post("/v1/auth/register")
            .content(objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
        .andExpect(status().isCreated());

    verify(authService).registerUser(request);
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "emailmail.com", "email@", "@mail.com", "email"})
  void whenRegisterUser_thenBadRequestReturned(String invalidEmail) throws Exception {
    var request = new RegisterUserRequest(FIRST_NAME, LAST_NAME, invalidEmail, PASSWORD);

    mockMvc.perform(post("/v1/auth/register")
            .content(objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isBadRequest());

    verify(authService, never()).registerUser(any());
  }

  @Test
  void whenLoginUser_thenSuccess() throws Exception {
    var loginRequest = new LoginRequest(EMAIL, PASSWORD);

    mockMvc.perform(post("/v1/auth/login")
            .content(objectMapper.writeValueAsBytes(loginRequest))
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk());

    verify(authService).loginUser(loginRequest);
  }

  @Test
  void whenLoginUser_thenBadRequest() throws Exception {
    var loginRequest = new LoginRequest("", "password");

    mockMvc.perform(post("/v1/auth/login")
            .content(objectMapper.writeValueAsString(loginRequest))
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isBadRequest());

    verify(authService, never()).loginUser(any());
  }
}
