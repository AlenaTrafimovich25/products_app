package com.andersen.products_app.unit_tests.service.impl;

import static com.andersen.utils.Constants.CATEGORY_ID;
import static com.andersen.utils.Constants.CATEGORY_NAME;
import static com.andersen.utils.Constants.PAGE_NUMBER;
import static com.andersen.utils.Constants.PAGE_SIZE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.andersen.products_app.entity.Category;
import com.andersen.products_app.exception.CategoryNotFoundException;
import com.andersen.products_app.repository.CategoryRepository;
import com.andersen.products_app.service.impl.CategoryServiceImpl;
import java.util.List;
import java.util.Optional;
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
class CategoryServiceImplTest {
  @InjectMocks
  private CategoryServiceImpl categoryService;

  @Mock
  private CategoryRepository categoryRepository;

  private Category category;
  private Pageable pageable;
  private Page<Category> categoryPage;

  @BeforeEach
  void setUp() {
    category = new Category(CATEGORY_ID, CATEGORY_NAME);

    pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);

    categoryPage = new PageImpl<>(List.of(category), pageable, pageable.getPageSize());
  }

  @Test
  void whenGetCategories_thenFindAll() {
    when(categoryRepository.findAll(any(Pageable.class))).thenReturn(categoryPage);

    categoryService.getCategories(pageable);

    verify(categoryRepository).findAll(pageable);
  }

  @Test
  void whenGetCategory_thenReturnCategoryEntity() {
    when(categoryRepository.findById(any())).thenReturn(Optional.of(category));

    categoryService.getCategory(CATEGORY_ID);

    verify(categoryRepository).findById(CATEGORY_ID);
  }

  @Test
  void whenGetCategory_thenCategoryNotFoundException() {
    when(categoryRepository.findById(any())).thenReturn(Optional.empty());

    var exception = assertThrows(CategoryNotFoundException.class, () ->
        categoryService.getCategory(CATEGORY_ID));

    assertEquals("Category with id 1 is not found", exception.getMessage());
    verify(categoryRepository).findById(CATEGORY_ID);
  }

  @Test
  void whenSaveCategory_thenCategoriesAdded() {
    categoryService.saveCategory(category);

    verify(categoryRepository).save(category);
  }

  @Test
  void whenDeleteCategory_thenCategoryDeleted() {
    categoryService.deleteCategory(CATEGORY_ID);

    verify(categoryRepository).deleteById(CATEGORY_ID);
  }

  @Test
  void whenExistsCategory_thenReturnTrue() {
    when(categoryRepository.existsById(any())).thenReturn(true);

    categoryService.existsCategory(CATEGORY_ID);

    verify(categoryRepository).existsById(CATEGORY_ID);
  }
}
