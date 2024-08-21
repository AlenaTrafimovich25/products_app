package com.andersen.products_app.service;

import com.andersen.products_app.entity.Product;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {

  Page<Product> getProducts(Pageable pageable);

  Page<Product> getProductsByCategoriesName(Set<String> categoryNames, Pageable pageable);

  Page<Product> getProductsByProductName(String productName, Pageable pageable);

  Page<String> getDistinctProductNames(Pageable pageable);

  Product getProduct(Long productId);

  Product saveProduct(Product product);

  void deleteProduct(Product product);
}
