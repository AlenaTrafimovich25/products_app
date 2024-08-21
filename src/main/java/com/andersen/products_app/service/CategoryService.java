package com.andersen.products_app.service;

import com.andersen.products_app.entity.Category;
import com.andersen.products_app.model.response.CategoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryService {

  Page<Category> getCategories(Pageable pageable);

  Category getCategory(Long id);

  Category saveCategory(Category category);

  void deleteCategory(Long categoryId);
}
