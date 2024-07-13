package com.andersen.products_app.unit_tests.service.impl;

import static com.andersen.utils.Constants.CATEGORY_NAME;
import static com.andersen.utils.Constants.LOGO;
import static com.andersen.utils.Constants.PAGE_NUMBER;
import static com.andersen.utils.Constants.PAGE_SIZE;
import static com.andersen.utils.Constants.PRODUCT_ID;
import static com.andersen.utils.Constants.PRODUCT_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.andersen.products_app.entity.Category;
import com.andersen.products_app.entity.Product;
import com.andersen.products_app.exception.ProductNotFoundException;
import com.andersen.products_app.repository.ProductRepository;
import com.andersen.products_app.service.impl.ProductServiceImpl;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
class ProductServiceImplTest {

  @InjectMocks
  private ProductServiceImpl productService;

  @Mock
  private ProductRepository productRepository;

  private Pageable pageable;
  private Product product;
  private Page<String> productNamesPage;
  private Page<Product> productsPage;

  @BeforeEach
  void setUp() {
    pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);

    var categoryEntity = new Category();
    categoryEntity.setName(CATEGORY_NAME);

    product =
        new Product(PRODUCT_ID, PRODUCT_NAME, LOGO, categoryEntity);

    productNamesPage = new PageImpl<>(List.of(PRODUCT_NAME), pageable, pageable.getPageSize());
    productsPage = new PageImpl<>(List.of(product), pageable, pageable.getPageSize());
  }

  @Test
  void whenGetProducts_theCallProductRepository() {
    when(productRepository.findAll(any(Pageable.class))).thenReturn(productsPage);

    productService.getProducts(pageable);

    verify(productRepository).findAll(pageable);
  }

  @Test
  void whenGetProductsByCategoriesName_theCallProductRepository() {
    when(
        productRepository.findProductsByCategoryNameIgnoreCaseIn(any(), any())).thenReturn(
        productsPage);

    productService.getProductsByCategoriesName(Set.of(CATEGORY_NAME), pageable);

    verify(productRepository).findProductsByCategoryNameIgnoreCaseIn(Set.of(CATEGORY_NAME),
        pageable);
  }

  @Test
  void whenGetProductsByProductName_theCallProductRepository() {
    when(productRepository.findProductsByNameContainingIgnoreCase(any(), any()))
        .thenReturn(productsPage);

    productService.getProductsByProductName(PRODUCT_NAME, pageable);

    verify(productRepository).findProductsByNameContainingIgnoreCase(PRODUCT_NAME,
        pageable);
  }

  @Test
  void whenGetDistinctProductNames_theCallProductRepository() {
    when(productRepository.findDistinctNames(any())).thenReturn(productNamesPage);

    productService.getDistinctProductNames(pageable);

    verify(productRepository).findDistinctNames(pageable);
  }

  @Test
  void whenGetProduct_theReturnProductEntity() {
    when(productRepository.findById(any())).thenReturn(Optional.of(product));

    productService.getProduct(PRODUCT_ID);

    verify(productRepository).findById(PRODUCT_ID);
  }

  @Test
  void whenGetProduct_theThrowProductNotFoundException() {
    when(productRepository.findById(any())).thenReturn(Optional.empty());

    var exception = assertThrows(ProductNotFoundException.class, () ->
        productService.getProduct(PRODUCT_ID));

    assertEquals("Product with id 1 is not found", exception.getMessage());
    verify(productRepository).findById(PRODUCT_ID);
  }

  @Test
  void whenSaveProduct_theProductSaved() {
    productService.saveProduct(product);

    verify(productRepository).save(product);
  }

  @Test
  void whenDeleteProduct_theProductDeleted() {
    productService.deleteProduct(product);

    verify(productRepository).delete(product);
  }
}
