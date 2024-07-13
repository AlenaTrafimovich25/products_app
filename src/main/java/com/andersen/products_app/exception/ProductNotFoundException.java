package com.andersen.products_app.exception;

public class ProductNotFoundException extends RuntimeException {

  public ProductNotFoundException(Long productId) {
    super(String.format("Product with id %d is not found", productId));
  }
}
