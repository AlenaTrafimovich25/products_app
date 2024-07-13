package com.andersen.products_app.exception.handler;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import com.andersen.products_app.exception.CategoryNotFoundException;
import com.andersen.products_app.exception.EmailNotFoundException;
import com.andersen.products_app.exception.ProductNotFoundException;
import com.andersen.products_app.exception.S3DeleteObjectException;
import com.andersen.products_app.exception.S3PutObjectException;
import com.andersen.products_app.exception.UserAlreadyExistsException;
import java.sql.SQLException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(ProductNotFoundException.class)
  public ResponseEntity<String> handleProductNotFoundException(
      ProductNotFoundException exception) {
    return new ResponseEntity<>(exception.getMessage(), NOT_FOUND);
  }

  @ExceptionHandler(CategoryNotFoundException.class)
  public ResponseEntity<String> handleCategoryFoundException(
      CategoryNotFoundException exception) {
    return new ResponseEntity<>(exception.getMessage(), NOT_FOUND);
  }

  @ExceptionHandler({S3DeleteObjectException.class, S3PutObjectException.class})
  public ResponseEntity<String> handleS3ObjectException(
      RuntimeException exception) {
    return new ResponseEntity<>(exception.getMessage(), BAD_REQUEST);
  }

  @ExceptionHandler(UserAlreadyExistsException.class)
  public ResponseEntity<String> handleUserAlreadyExistsException(
      UserAlreadyExistsException exception) {
    return new ResponseEntity<>(exception.getMessage(), CONFLICT);
  }

  @ExceptionHandler(EmailNotFoundException.class)
  public ResponseEntity<String> handleEmailNotFoundException(
      EmailNotFoundException exception) {
    return new ResponseEntity<>(exception.getMessage(), NOT_FOUND);
  }

  @ExceptionHandler({SQLException.class})
  public ResponseEntity<String> handleSQLException(SQLException exception) {
    return new ResponseEntity<>(exception.getMessage(), BAD_REQUEST);
  }
}
