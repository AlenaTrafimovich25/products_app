package com.andersen.products_app.service;

import com.andersen.products_app.entity.Product;
import java.util.List;
import java.util.Optional;

public interface CacheService {

  void put(Product product);

  Optional<Product> get(Long productId);

  List<Product> getAll();

  void evict(Product product);
}
