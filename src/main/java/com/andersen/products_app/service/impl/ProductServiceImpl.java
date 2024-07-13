package com.andersen.products_app.service.impl;

import com.andersen.products_app.entity.Product;
import com.andersen.products_app.exception.ProductNotFoundException;
import com.andersen.products_app.repository.ProductRepository;
import com.andersen.products_app.service.ProductService;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {
  private final ProductRepository productRepository;

  public ProductServiceImpl(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  @Override
  public Page<Product> getProducts(Pageable pageable) {
    return productRepository.findAll(pageable);
  }

  @Override
  public Page<Product> getProductsByCategoriesName(Set<String> categoryNames,
                                                   Pageable pageable) {
    return productRepository.findProductsByCategoryNameIgnoreCaseIn(
        categoryNames,
        pageable);
  }

  @Override
  public Page<Product> getProductsByProductName(String productName, Pageable pageable) {
    return productRepository.findProductsByNameContainingIgnoreCase(productName,
        pageable);
  }

  @Override
  public Page<String> getDistinctProductNames(Pageable pageable) {
    return productRepository.findDistinctNames(pageable);
  }

  @Override
  public Product getProduct(Long productId) {
    return productRepository.findById(productId)
        .orElseThrow(() -> new ProductNotFoundException(productId));
  }

  @Override
  public void saveProduct(Product product) {
    productRepository.save(product);
  }

  @Override
  public void deleteProduct(Product product) {
    productRepository.delete(product);
  }
}

