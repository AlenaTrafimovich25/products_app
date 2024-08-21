package com.andersen.products_app.service.impl;

import com.andersen.products_app.mapper.CategoryMapper;
import com.andersen.products_app.mapper.ProductMapper;
import com.andersen.products_app.model.request.CategoryCreationRequest;
import com.andersen.products_app.model.request.ProductCreationRequest;
import com.andersen.products_app.model.request.ProductUpdateRequest;
import com.andersen.products_app.model.response.CategoryResponse;
import com.andersen.products_app.model.response.ProductResponse;
import com.andersen.products_app.service.CategoryService;
import com.andersen.products_app.service.ProductService;
import com.andersen.products_app.service.S3Service;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProductsFacade {
  private final ProductService productService;
  private final ProductMapper productMapper;
  private final S3Service s3service;
  private final CategoryService categoryService;
  private final CategoryMapper categoryMapper;

  public ProductsFacade(ProductService productService,
                        ProductMapper productMapper,
                        S3Service s3service, CategoryService categoryService,
                        CategoryMapper categoryMapper) {
    this.productService = productService;
    this.productMapper = productMapper;
    this.s3service = s3service;
    this.categoryService = categoryService;
    this.categoryMapper = categoryMapper;
  }

  public Page<ProductResponse> getProducts(Set<String> categories, String name, Pageable pageable) {
    if (!StringUtils.isBlank(name)) {
      return productService.getProductsByProductName(name, pageable)
          .map(productMapper::toProductResponse);
    }

    if (!CollectionUtils.isEmpty(categories)) {
      return productService.getProductsByCategoriesName(categories, pageable)
          .map(productMapper::toProductResponse);
    }

    return productService.getProducts(pageable)
        .map(productMapper::toProductResponse);
  }

  public Page<String> getDistinctProductNames(Pageable pageable) {
    return productService.getDistinctProductNames(pageable);
  }

  public ProductResponse getProduct(Long productId) {
    var product = productService.getProduct(productId);
    return productMapper.toProductResponse(product);
  }

  @Transactional
  public void updateProduct(Long productId, ProductUpdateRequest productUpdateRequest,
                            MultipartFile file) {
    var product = productService.getProduct(productId);

    if (!StringUtils.isEmpty(productUpdateRequest.name())) {
      product.setName(productUpdateRequest.name());
    }

    if (file != null && !file.isEmpty()) {
      var key = product.getLogo().replaceAll(".+/", "");
      var url = s3service.putS3Object(key, file);
      product.setLogo(url);
    }
    productService.saveProduct(product);
  }

  @Transactional
  public ProductResponse createProduct(ProductCreationRequest productCreationRequest,
                                       MultipartFile file) {
    var category = categoryService.getCategory(productCreationRequest.categoryId());
    var logo = s3service.putS3Object(UUID.randomUUID().toString(), file);
    var product = productMapper.toProductEntity(productCreationRequest, logo, category);
    return productMapper.toProductResponse(productService.saveProduct(product));
  }

  @Transactional
  public void deleteProduct(Long productId) {
    var product = productService.getProduct(productId);
    s3service.deleteFile(product.getLogo());
    productService.deleteProduct(product);
  }

  public CategoryResponse getCategory(Long categoryId, Pageable pageable) {
    var categoryEntity = categoryService.getCategory(categoryId);

    var products = categoryEntity.getProducts()
        .stream()
        .map(productMapper::toProductResponse)
        .toList();

    return new CategoryResponse(categoryEntity.getId(),
        categoryEntity.getName(), getPage(products, pageable));
  }

  public Page<CategoryResponse> getCategories(Pageable pageable) {
    return categoryService.getCategories(pageable)
        .map(categoryMapper::toCategoryResponse);
  }

  public CategoryResponse createCategory(CategoryCreationRequest categoryCreationRequest) {
    var category =
        categoryService.saveCategory(categoryMapper.toCategoryEntity(categoryCreationRequest));
    return categoryMapper.toCategoryResponse(category);
  }

  @Transactional
  public void deleteCategory(Long categoryId) {
    var category = categoryService.getCategory(categoryId);

    category.getProducts()
        .forEach(productService::deleteProduct);
    categoryService.deleteCategory(categoryId);
  }

  private <T> Page<T> getPage(List<T> filteredItems, Pageable pageable) {
    return new PageImpl<>(filteredItems, pageable, pageable.getPageSize());
  }
}
