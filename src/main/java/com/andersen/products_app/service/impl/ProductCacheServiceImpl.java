package com.andersen.products_app.service.impl;

import com.andersen.products_app.entity.Product;
import com.andersen.products_app.service.ProductCacheService;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ProductCacheServiceImpl implements ProductCacheService {
  private final JpaRepository<Product, Long> repository;
  private Map<Long, Product> products = new ConcurrentHashMap<>();

  public ProductCacheServiceImpl(JpaRepository<Product, Long> repository) {
    this.repository = repository;
  }

  @PostConstruct
  @Scheduled(cron = "${scheduler.cache.period}")
  public void loadCache() {
    this.products = repository.findAll().stream()
        .collect(Collectors.toConcurrentMap(Product::getId, Function.identity()));
  }

  public void put(Product entity) {
    products.put(entity.getId(), entity);
  }

  public Optional<Product> get(Long productId) {
    return Optional.ofNullable(products.get(productId));
  }

  public List<Product> getAll() {
    return products.values().stream()
        .toList();
  }

  public void evict(Product entity) {
    products.remove(entity.getId());
  }
}
