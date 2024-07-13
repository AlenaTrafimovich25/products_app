package com.andersen.products_app.service;


import com.andersen.products_app.model.request.LoginRequest;
import com.andersen.products_app.model.request.RegisterUserRequest;
import com.andersen.products_app.model.response.LoginResponse;

public interface AuthService {

  LoginResponse loginUser(LoginRequest loginRequest);

  void registerUser(RegisterUserRequest registerUserRequest);
}
