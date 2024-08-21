package com.andersen.products_app.unit_tests.service.impl;

import static com.andersen.utils.Constants.EMAIL;
import static com.andersen.utils.Constants.FIRST_NAME;
import static com.andersen.utils.Constants.LAST_NAME;
import static com.andersen.utils.Constants.PASSWORD;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.andersen.products_app.entity.User;
import com.andersen.products_app.exception.UserAlreadyExistsException;
import com.andersen.products_app.mapper.UserMapper;
import com.andersen.products_app.mapper.UserMapperImpl;
import com.andersen.products_app.model.request.LoginRequest;
import com.andersen.products_app.model.request.RegisterUserRequest;
import com.andersen.products_app.repository.UserRepository;
import com.andersen.products_app.service.impl.AuthServiceImpl;
import com.andersen.products_app.service.impl.JwtService;
import com.andersen.products_app.service.impl.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

  @Mock
  private JwtService jwtService;

  @Mock
  private UserRepository userRepository;

  @Spy
  private UserMapper userMapper = new UserMapperImpl();


  @Mock
  private UserDetailsServiceImpl userDetailsService;

  @Mock
  private AuthenticationManager authenticationManager;

  @InjectMocks
  private AuthServiceImpl authService;

  @Mock
  private Authentication authentication;

  private final LoginRequest loginRequest = new LoginRequest(EMAIL, PASSWORD);

  private final RegisterUserRequest registerUserRequest =
      new RegisterUserRequest(FIRST_NAME, LAST_NAME, EMAIL, PASSWORD);

  @Test
  void whenAuthUser_thenSuccess() {
    when(authenticationManager.authenticate(any())).thenReturn(authentication);
    when(authentication.getName()).thenReturn(EMAIL);

    authService.loginUser(loginRequest);

    verify(jwtService).generateToken(loginRequest.email());
  }

  @Test
  void whenRegisterUser_thenSuccess() {
    when(userDetailsService.existsUser(EMAIL)).thenReturn(false);
    when(userMapper.toUser(registerUserRequest)).thenReturn(new User());

    authService.registerUser(registerUserRequest);

    verify(userDetailsService).createUser(any(User.class));
  }

  @Test
  void whenRegisterExistingUser_thenUserAlreadyExistsExceptionThrown() {
    when(userDetailsService.existsUser(EMAIL)).thenReturn(true);

    assertThrows(UserAlreadyExistsException.class,
        () -> authService.registerUser(registerUserRequest));

    verify(userDetailsService, times(0)).createUser(any());
  }
}
