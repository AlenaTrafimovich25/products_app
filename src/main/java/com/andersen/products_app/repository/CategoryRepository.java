package com.andersen.products_app.repository;

import com.andersen.products_app.entity.Category;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

  @EntityGraph(attributePaths = "products")
  Optional<Category> findById(Long id);
}
