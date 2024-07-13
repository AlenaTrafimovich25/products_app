package com.andersen.products_app.integration_tests.repository;

import static com.andersen.utils.Constants.CATEGORY_ID;
import static com.andersen.utils.Constants.CATEGORY_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.andersen.products_app.entity.Category;
import com.andersen.products_app.repository.CategoryRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
public class CategoryRepositoryIntegrationTest {

  @Autowired
  private CategoryRepository categoryRepository;

  @Container
  public static PostgreSQLContainer<?> postgres =
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
  void setUP() {
    postgres.withReuse(true);
  }

  @Test
  @Transactional
  void whenFindsByCategoryNameIn_ThenReturnProduct() {
    var categoryEntity = new Category();
    categoryEntity.setName(CATEGORY_NAME);
    categoryEntity.setId(CATEGORY_ID);

    var categoryEntity2 = new Category();
    categoryEntity2.setName("ANOTHER_CATEGORY");
    categoryEntity2.setId(2L);

    categoryRepository.saveAndFlush(categoryEntity);
    categoryRepository.saveAndFlush(categoryEntity2);

    var category =
        categoryRepository.findById(CATEGORY_ID);

    assertEquals(Optional.of(categoryEntity), category);
  }

  @Test
  @Transactional
  void whenFindsByCategoryEntityNameIn_ThenOptionalEmpty() {
    var category =
        categoryRepository.findById(CATEGORY_ID);

    assertEquals(Optional.empty(), category);
  }
}
