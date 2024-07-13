package com.andersen.products_app.service;

import com.andersen.products_app.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryService {

  Page<Category> getCategories(Pageable pageable);

  Category getCategory(Long id);

  void saveCategory(Category category);

  void deleteCategory(Long categoryId);

  boolean existsCategory(Long categoryId);
}
