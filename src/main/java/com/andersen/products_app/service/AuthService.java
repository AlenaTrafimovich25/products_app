package com.andersen.products_app.service;


import com.andersen.products_app.model.request.LoginRequest;
import com.andersen.products_app.model.request.RegisterUserRequest;
import com.andersen.products_app.model.response.LoginResponse;

public interface AuthService {

  void registerUser(RegisterUserRequest registerUserRequest);

  LoginResponse loginUser(LoginRequest loginRequest);
}
