package com.andersen.products_app.controller;

import com.andersen.products_app.model.request.LoginRequest;
import com.andersen.products_app.model.request.RegisterUserRequest;
import com.andersen.products_app.model.response.LoginResponse;
import com.andersen.products_app.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
@Tag(name = "Auth Controller", description = "User Authentication")
public class AuthController {

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/register")
  @ResponseStatus(HttpStatus.CREATED)
  @ApiResponse(responseCode = "201", description = "CREATED")
  @ApiResponse(responseCode = "400", description = "BAD REQUEST")
  @ApiResponse(responseCode = "409", description = "Conflict")
  @Operation(summary = "Register user")
  public void registerUser
      (@RequestBody @Valid RegisterUserRequest registerRequest) {
    authService.registerUser(registerRequest);
  }

  @PostMapping("/login")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponse(responseCode = "200", description = "OK")
  @ApiResponse(responseCode = "400", description = "BAD REQUEST")
  @Operation(summary = "Login user")
  public LoginResponse authenticateUser
      (@RequestBody @Valid LoginRequest loginRequest) {
    return authService.loginUser(loginRequest);
  }
}
