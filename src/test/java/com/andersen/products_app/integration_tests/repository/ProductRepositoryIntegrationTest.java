package com.andersen.products_app.integration_tests.repository;

import static com.andersen.utils.Constants.CATEGORY_ID;
import static com.andersen.utils.Constants.CATEGORY_NAME;
import static com.andersen.utils.Constants.LOGO;
import static com.andersen.utils.Constants.PAGE_SIZE;
import static com.andersen.utils.Constants.PRODUCT_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.andersen.products_app.entity.Category;
import com.andersen.products_app.entity.Product;
import com.andersen.products_app.repository.ProductRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class ProductRepositoryIntegrationTest {
  private Pageable pageable;
  private Product product;
  private Product product2;
  private Page<String> productNamesPage;
  private Page<Product> productsPage;

  @Autowired
  private ProductRepository productRepository;

  @Container
  static PostgreSQLContainer<?> postgres =
      new PostgreSQLContainer<>("postgres:11.1")
          .withDatabaseName("DB")
          .withUsername("postgres")
          .withPassword("postgres");

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
  }

  @BeforeEach
  void setUp() {
    postgres.withReuse(true);

    pageable = PageRequest.of(0, PAGE_SIZE);

    var categoryEntity = new Category();
    categoryEntity.setName(CATEGORY_NAME);
    categoryEntity.setId(CATEGORY_ID);

    var categoryEntity2 = new Category();
    categoryEntity2.setName("ANOTHER_CATEGORY");
    categoryEntity2.setId(2L);

    product =
        new Product(null, PRODUCT_NAME, LOGO, categoryEntity);
    product2 =
        new Product(null, "anotherName", "anotherLogo", categoryEntity2);

    productsPage = new PageImpl<>(List.of(product), pageable, pageable.getPageSize());
    productNamesPage =
        new PageImpl<>(List.of("anotherName", PRODUCT_NAME), pageable, pageable.getPageSize());
  }

  @Test
  @Transactional
  void whenFindProductsByNameContainingIgnoreCase_ThenReturnProduct() {
    productRepository.save(product);
    productRepository.save(product2);

    var product =
        productRepository.findProductsByNameContainingIgnoreCase(PRODUCT_NAME,
            pageable);

    assertEquals(productsPage.getContent(), product.getContent());
  }

  @Test
  @Transactional
  void whenFindDistinctNames_ThenReturnProductName() {
    productRepository.save(product);
    productRepository.save(product);
    productRepository.save(product2);

    var productNames =
        productRepository.findDistinctNames(pageable);

    assertEquals(productNamesPage.getContent(), productNames.getContent());
  }
}
