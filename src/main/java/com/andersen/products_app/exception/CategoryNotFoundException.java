package com.andersen.products_app.exception;

public class CategoryNotFoundException extends RuntimeException {

  public CategoryNotFoundException(Long categoryId) {
    super(String.format("Category with id %d is not found", categoryId));
  }
}
