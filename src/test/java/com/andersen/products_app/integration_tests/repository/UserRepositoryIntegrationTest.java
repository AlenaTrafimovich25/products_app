package com.andersen.products_app.integration_tests.repository;

import static com.andersen.products_app.model.enums.Role.USER;
import static com.andersen.utils.Constants.EMAIL;
import static com.andersen.utils.Constants.FIRST_NAME;
import static com.andersen.utils.Constants.LAST_NAME;
import static com.andersen.utils.Constants.PASSWORD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.andersen.products_app.entity.User;
import com.andersen.products_app.repository.UserRepository;
import java.util.Optional;
import java.util.Set;
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
public class UserRepositoryIntegrationTest {

  @Autowired
  private UserRepository userRepository;

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
  void setUp() {
    postgres.withReuse(true);
  }

  @Test
  @Transactional
  void whenFindByEmail_ThenReturnUserOptional() {
    var userEntity = new User();
    userEntity.setFirstName(FIRST_NAME);
    userEntity.setLastName(LAST_NAME);
    userEntity.setEmail(EMAIL);
    userEntity.setPassword(PASSWORD);
    userEntity.addRole(USER);

    var userEntity2 = new User();
    userEntity2.setFirstName(FIRST_NAME);
    userEntity2.setLastName(LAST_NAME);
    userEntity2.setEmail("ANOTHER_EMAIL");
    userEntity2.setPassword(PASSWORD);
    userEntity2.addRole(USER);

    userRepository.saveAll(Set.of(userEntity, userEntity2));

    var user =
        userRepository.findByEmail(EMAIL);

    assertEquals(Optional.of(userEntity), user);
  }

  @Test
  @Transactional
  void whenFindByEmail_ThenReturnUserOptionalEmpty() {
    var user =
        userRepository.findByEmail(EMAIL);

    assertEquals(Optional.empty(), user);
  }

  @Test
  @Transactional
  void whenExistsByEmail_ThenReturnTrue() {
    var userEntity = new User();
    userEntity.setFirstName(FIRST_NAME);
    userEntity.setLastName(LAST_NAME);
    userEntity.setEmail(EMAIL);
    userEntity.setPassword(PASSWORD);
    userEntity.addRole(USER);

    var userEntity2 = new User();
    userEntity2.setFirstName(FIRST_NAME);
    userEntity2.setLastName(LAST_NAME);
    userEntity2.setEmail("ANOTHER_EMAIL");
    userEntity2.setPassword(PASSWORD);
    userEntity2.addRole(USER);

    userRepository.saveAll(Set.of(userEntity, userEntity2));

    var existsByEmail =
        userRepository.existsByEmail(EMAIL);

    assertTrue(existsByEmail);
  }

  @Test
  @Transactional
  void whenExistsByEmail_ThenReturnFalse() {
    var existsByEmail =
        userRepository.existsByEmail(EMAIL);

    assertFalse(existsByEmail);
  }
}
