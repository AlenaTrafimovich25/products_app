package com.andersen.products_app.exception;

public class S3PutObjectException extends RuntimeException {

  public S3PutObjectException(String message) {
    super(message);
  }
}
