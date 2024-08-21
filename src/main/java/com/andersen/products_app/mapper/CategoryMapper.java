package com.andersen.products_app.mapper;

import com.andersen.products_app.entity.Category;
import com.andersen.products_app.entity.Product;
import com.andersen.products_app.model.request.CategoryCreationRequest;
import com.andersen.products_app.model.response.CategoryResponse;
import com.andersen.products_app.model.response.ProductResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

  Category toCategoryEntity(CategoryCreationRequest categoryCreationRequest);

  ProductResponse toProductResponse(Product product);

  CategoryResponse toCategoryResponse(Category category);
}
