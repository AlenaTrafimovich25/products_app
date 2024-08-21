package com.andersen.products_app.unit_tests.aspect;

import static com.andersen.utils.Constants.CATEGORY_ID;
import static com.andersen.utils.Constants.CATEGORY_NAME;
import static com.andersen.utils.Constants.LOGO;
import static com.andersen.utils.Constants.PAGE_NUMBER;
import static com.andersen.utils.Constants.PAGE_SIZE;
import static com.andersen.utils.Constants.PRODUCT_ID;
import static com.andersen.utils.Constants.PRODUCT_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.andersen.products_app.aspect.CacheAspect;
import com.andersen.products_app.entity.Category;
import com.andersen.products_app.entity.Product;
import com.andersen.products_app.service.impl.ProductCacheServiceImpl;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class CacheAspectTest {
  @Mock
  private ProceedingJoinPoint joinPoint;

  @InjectMocks
  private CacheAspect cacheAspect;
  @Mock
  private ProductCacheServiceImpl productCache;

  private Product product1;
  private Product product2;
  private Pageable pageable;
  private Page<String> productNamesPage;
  private Page<Product> productsPage;

  @BeforeEach
  public void setUp() {
    var categoryEntity1 = new Category(CATEGORY_ID, CATEGORY_NAME);
    var categoryEntity2 = new Category(2L, "another");

    product1 = new Product(PRODUCT_ID, PRODUCT_NAME, LOGO, categoryEntity1);
    product2 = new Product(2L, "Product 2", "Logo 2", categoryEntity2);

    pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);

    productsPage = new PageImpl<>(List.of(product1), pageable, pageable.getPageSize());
    productNamesPage =
        new PageImpl<>(List.of(PRODUCT_NAME, product2.getName()), pageable, pageable.getPageSize());
  }

  @Test
  void whenGetAllProducts_thenProductsProvided() {
    when(productCache.getAll()).thenReturn(List.of(product1, product2));
    when(joinPoint.getArgs()).thenReturn(new Object[] {pageable});

    cacheAspect.getProducts(joinPoint);

    verify(productCache).getAll();
  }

  @Test
  void whenGetProductsByCategoriesName_thenProductsProvided() throws Throwable {
    when(productCache.getAll()).thenReturn(List.of(product1, product2));
    when(joinPoint.getArgs()).thenReturn(new Object[] {Set.of(CATEGORY_NAME), pageable});

    var result = cacheAspect.getProductsByCategoriesName(joinPoint);

    assertEquals(productsPage, result);

    verify(joinPoint, never()).proceed();
    verify(productCache).getAll();
  }

  @Test
  void whenGetProductsByProductNameIgnoreCase_thenGetProducts() throws Throwable {
    when(productCache.getAll()).thenReturn(List.of(product1, product2));
    when(joinPoint.getArgs()).thenReturn(new Object[] {PRODUCT_NAME.toUpperCase(), pageable});

    var result = cacheAspect.getProductsByProductName(joinPoint);

    assertEquals(productsPage, result);
    verify(joinPoint, never()).proceed();
    verify(productCache).getAll();
  }

  @Test
  void whenGetProductsByProductById_thenGetProduct() throws Throwable {
    when(productCache.get(any())).thenReturn(Optional.of(product1));
    when(joinPoint.getArgs()).thenReturn(new Object[] {PRODUCT_ID});

    var result = cacheAspect.getProductById(joinPoint);

    assertEquals(Optional.of(product1), result);
    verify(joinPoint, never()).proceed();
    verify(productCache).get(PRODUCT_ID);
  }

  @Test
  void whenGetProductNames_thenProductNamesProvided() throws Throwable {
    when(productCache.getAll()).thenReturn(List.of(product1, product2));
    when(joinPoint.getArgs()).thenReturn(new Object[] {pageable});

    var result = cacheAspect.getProductsNames(joinPoint);

    assertEquals(productNamesPage, result);
    verify(joinPoint, never()).proceed();
    verify(productCache).getAll();
  }

  @Test
  void whenSaveProduct_thenProductSaved() throws Throwable {
    when(joinPoint.proceed()).thenReturn(product1);

    cacheAspect.saveProduct(joinPoint);

    verify(productCache).put(product1);
  }

  @Test
  void whenDeleteProduct_thenProductDeleted() throws Throwable {
    when(joinPoint.proceed()).thenReturn(product1);

    cacheAspect.deleteProduct(joinPoint);

    verify(productCache).evict(product1);
  }
}
