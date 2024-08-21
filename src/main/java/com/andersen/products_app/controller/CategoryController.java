package com.andersen.products_app.controller;

import com.andersen.products_app.model.request.CategoryCreationRequest;
import com.andersen.products_app.model.response.CategoryResponse;
import com.andersen.products_app.service.impl.ProductsFacade;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/categories")
@Tag(name = "Categories Controller", description = "Categories Info")
public class CategoryController {
  private final ProductsFacade productsFacade;

  public CategoryController(ProductsFacade productsFacade) {
    this.productsFacade = productsFacade;
  }

  @GetMapping("/{categoryId}")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponse(responseCode = "200", description = "OK")
  @ApiResponse(responseCode = "404", description = "NOT FOUND")
  @SecurityRequirement(name = "bearerAuth")
  public CategoryResponse getCategory(
      @PathVariable Long categoryId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    return productsFacade.getCategory(categoryId, PageRequest.of(page, size));
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  @ApiResponse(responseCode = "200", description = "OK")
  @SecurityRequirement(name = "bearerAuth")
  public Page<CategoryResponse> getCategories(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    return productsFacade.getCategories(PageRequest.of(page, size));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @ApiResponse(responseCode = "200", description = "OK")
  @ApiResponse(responseCode = "400", description = "BAD REQUEST")
  @ApiResponse(responseCode = "401", description = "UNAUTHORIZED")
  @SecurityRequirement(name = "bearerAuth")
  public CategoryResponse addCategory(
      @RequestBody @Valid CategoryCreationRequest categoryCreationRequest) {
    return productsFacade.createCategory(categoryCreationRequest);
  }

  @DeleteMapping("/{categoryId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ApiResponse(responseCode = "204", description = "NO_CONTENT")
  @ApiResponse(responseCode = "400", description = "BAD REQUEST")
  @ApiResponse(responseCode = "401", description = "UNAUTHORIZED")
  @SecurityRequirement(name = "bearerAuth")
  public void deleteCategory(
      @PathVariable Long categoryId) {
    productsFacade.deleteCategory(categoryId);
  }
}
