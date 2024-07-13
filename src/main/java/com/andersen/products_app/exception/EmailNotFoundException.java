package com.andersen.products_app.exception;

public class EmailNotFoundException extends RuntimeException {

  public EmailNotFoundException(String email) {
    super(String.format("Email with email %s is not found", email));
  }
}
