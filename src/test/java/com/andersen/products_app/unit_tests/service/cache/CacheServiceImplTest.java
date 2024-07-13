package com.andersen.products_app.unit_tests.service.cache;

import static com.andersen.utils.Constants.LOGO;
import static com.andersen.utils.Constants.PRODUCT_ID;
import static com.andersen.utils.Constants.PRODUCT_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.andersen.products_app.entity.Product;
import com.andersen.products_app.service.CacheService;
import com.andersen.products_app.service.impl.CacheServiceImpl;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class CacheServiceImplTest {
  private CacheService cacheService;
  private Map<Long, Product> cacheMap;
  private Product product1;
  private Product product2;

  @BeforeEach
  void setUp() {
    cacheService = new CacheServiceImpl(null);

    product1 = new Product(PRODUCT_ID, PRODUCT_NAME, LOGO, null);
    product2 = new Product(2L, "Product 2", "Logo 2", null);

    cacheMap = new ConcurrentHashMap<>();

    cacheMap.put(product1.getId(), product1);

    ReflectionTestUtils.setField(cacheService, "products", cacheMap);
  }

  @Test
  void whenGetById_thenSuccess() {
    var result = cacheService.get(PRODUCT_ID);

    assertEquals(Optional.of(product1), result);
  }

  @Test
  void whenGetAll_thenSuccess() {
    var result = cacheService.getAll();

    assertEquals(List.of(product1), result);
  }

  @Test
  void whenPut_thenSaved() {
    cacheService.put(product2);

    assertEquals(2, cacheMap.size());
  }

  @Test
  void whenEvict_thenDeleted() {
    cacheService.evict(product1);

    assertEquals(0, cacheMap.size());
  }
}
