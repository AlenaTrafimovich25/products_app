package com.andersen.products_app.exception;

public class S3DeleteObjectException extends RuntimeException {

  public S3DeleteObjectException(String message) {
    super(message);
  }
}
