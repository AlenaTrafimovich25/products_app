package com.andersen.products_app.controller;

import com.andersen.products_app.model.request.ProductCreationRequest;
import com.andersen.products_app.model.request.ProductUpdateRequest;
import com.andersen.products_app.model.response.ProductResponse;
import com.andersen.products_app.service.impl.ProductsFacade;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("v1/products")
@Tag(name = "Products Controller", description = "Products Info")
public class ProductController {
  private final ProductsFacade productsFacade;

  public ProductController(ProductsFacade productsFacade) {
    this.productsFacade = productsFacade;
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  @ApiResponse(responseCode = "200", description = "OK")
  @ApiResponse(responseCode = "400", description = "BAD REQUEST")
  @SecurityRequirement(name = "bearerAuth")
  public Page<ProductResponse> getProducts(
      @RequestParam(required = false) Set<String> categories,
      @RequestParam(required = false) String name,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    var pageable = PageRequest.of(page, size);
    return productsFacade.getProducts(categories, name, pageable);
  }

  @GetMapping("/{productId}")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponse(responseCode = "200", description = "OK")
  @ApiResponse(responseCode = "400", description = "BAD REQUEST")
  @ApiResponse(responseCode = "404", description = "NOT_FOUND")
  @SecurityRequirement(name = "bearerAuth")
  public ProductResponse getProductById(
      @PathVariable Long productId) {
    return productsFacade.getProduct(productId);
  }

  @GetMapping("/names")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponse(responseCode = "200", description = "OK")
  @ApiResponse(responseCode = "400", description = "BAD REQUEST")
  @SecurityRequirement(name = "bearerAuth")
  public Page<String> getProductNames(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    var pageable = PageRequest.of(page, size);
    return productsFacade.getDistinctProductNames(pageable);
  }

  @PutMapping(value = "/{productId}",
      consumes = {
          MediaType.MULTIPART_FORM_DATA_VALUE,
          MediaType.APPLICATION_JSON_VALUE})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ApiResponse(responseCode = "200", description = "OK")
  @ApiResponse(responseCode = "400", description = "BAD REQUEST")
  @ApiResponse(responseCode = "404", description = "NOT_FOUND")
  @SecurityRequirement(name = "bearerAuth")
  public void updateProduct(
      @PathVariable Long productId,
      @RequestPart ProductUpdateRequest productUpdateRequest,
      @RequestPart MultipartFile file) {
    productsFacade.updateProduct(productId, productUpdateRequest, file);
  }

  @PostMapping(consumes = {
      MediaType.MULTIPART_FORM_DATA_VALUE,
      MediaType.APPLICATION_JSON_VALUE})
  @ResponseStatus(HttpStatus.CREATED)
  @ApiResponse(responseCode = "200", description = "OK")
  @ApiResponse(responseCode = "400", description = "BAD REQUEST")
  @SecurityRequirement(name = "bearerAuth")
  public void addProduct(
      @RequestPart @Valid ProductCreationRequest productCreationRequest,
      @RequestPart @NotNull MultipartFile file) {
    productsFacade.createProduct(productCreationRequest, file);
  }

  @DeleteMapping("/{productId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ApiResponse(responseCode = "200", description = "OK")
  @ApiResponse(responseCode = "400", description = "BAD REQUEST")
  @ApiResponse(responseCode = "404", description = "NOT_FOUND")
  @SecurityRequirement(name = "bearerAuth")
  public void deleteProduct(@PathVariable Long productId) {
    productsFacade.deleteProduct(productId);
  }
}
