package com.andersen.products_app.service.impl;

import com.andersen.products_app.entity.Category;
import com.andersen.products_app.exception.CategoryNotFoundException;
import com.andersen.products_app.repository.CategoryRepository;
import com.andersen.products_app.service.CategoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl implements CategoryService {
  private final CategoryRepository categoryRepository;

  public CategoryServiceImpl(CategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
  }

  @Override
  public Category getCategory(Long categoryId) {
    return categoryRepository.findById(categoryId)
        .orElseThrow(() -> new CategoryNotFoundException(categoryId));
  }

  @Override
  public Page<Category> getCategories(Pageable pageable) {
    return categoryRepository.findAll(pageable);
  }

  @Override
  public void saveCategory(Category category) {
    categoryRepository.save(category);
  }

  @Override
  public void deleteCategory(Long categoryId) {
    categoryRepository.deleteById(categoryId);
  }
}

