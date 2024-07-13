package com.andersen.products_app.repository;

import com.andersen.products_app.entity.Product;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

  Page<Product> findProductsByCategoryNameIgnoreCaseIn(
      Set<String> categoryNames,
      Pageable pageable);

  Page<Product> findProductsByNameContainingIgnoreCase(String productName,
                                                       Pageable pageable);

  @Query("SELECT DISTINCT p.name FROM Product p")
  Page<String> findDistinctNames(Pageable pageable);
}
