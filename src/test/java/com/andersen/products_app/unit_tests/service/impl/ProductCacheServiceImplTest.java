package com.andersen.products_app.unit_tests.service.impl;

import static com.andersen.utils.Constants.LOGO;
import static com.andersen.utils.Constants.PRODUCT_ID;
import static com.andersen.utils.Constants.PRODUCT_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.andersen.products_app.entity.Product;
import com.andersen.products_app.service.ProductCacheService;
import com.andersen.products_app.service.impl.ProductCacheServiceImpl;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class ProductCacheServiceImplTest {
  private ProductCacheService productCacheService;
  private Map<Long, Product> cacheMap;
  private Product product1;
  private Product product2;

  @BeforeEach
  void setUp() {
    productCacheService = new ProductCacheServiceImpl(null);

    product1 = new Product(PRODUCT_ID, PRODUCT_NAME, LOGO, null);
    product2 = new Product(2L, "Product 2", "Logo 2", null);

    cacheMap = new ConcurrentHashMap<>();

    cacheMap.put(product1.getId(), product1);

    ReflectionTestUtils.setField(productCacheService, "products", cacheMap);
  }

  @Test
  void whenGetById_thenSuccess() {
    var result = productCacheService.get(PRODUCT_ID);

    assertEquals(Optional.of(product1), result);
  }

  @Test
  void whenGetAll_thenSuccess() {
    var result = productCacheService.getAll();

    assertEquals(List.of(product1), result);
  }

  @Test
  void whenPut_thenSaved() {
    productCacheService.put(product2);

    assertEquals(2, cacheMap.size());
  }

  @Test
  void whenEvict_thenDeleted() {
    productCacheService.evict(product1);

    assertEquals(0, cacheMap.size());
  }
}
