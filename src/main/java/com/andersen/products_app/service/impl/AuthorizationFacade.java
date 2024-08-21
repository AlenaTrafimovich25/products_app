package com.andersen.products_app.service.impl;

import static com.andersen.products_app.model.enums.Role.USER;

import com.andersen.products_app.exception.UserAlreadyExistsException;
import com.andersen.products_app.mapper.UserMapper;
import com.andersen.products_app.model.request.LoginRequest;
import com.andersen.products_app.model.request.RegisterUserRequest;
import com.andersen.products_app.model.response.LoginResponse;
import com.andersen.products_app.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationFacade implements AuthService {
  private final JwtService jwtService;
  private final UserMapper userMapper;
  private final UserDetailsServiceImpl userDetailsService;
  private final AuthenticationManager authenticationManager;

  public AuthorizationFacade(JwtService jwtService,
                             UserMapper userMapper,
                             UserDetailsServiceImpl userDetailsService,
                             AuthenticationManager authenticationManager) {
    this.jwtService = jwtService;
    this.userMapper = userMapper;
    this.userDetailsService = userDetailsService;
    this.authenticationManager = authenticationManager;
  }

  @Override
  public LoginResponse loginUser(LoginRequest loginRequest) {
    var authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password()));
    return new LoginResponse(jwtService.generateToken(authentication.getName()));
  }

  @Override
  public void registerUser(RegisterUserRequest registerUserRequest) {
    if (userDetailsService.existsUser(registerUserRequest.email())) {
      throw new UserAlreadyExistsException(registerUserRequest.email());
    }

    var userEntity = userMapper.toUser(registerUserRequest);
    userEntity.addRole(USER);
    userDetailsService.createUser(userEntity);
  }
}
