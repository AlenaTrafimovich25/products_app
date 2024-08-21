package com.andersen.products_app.mapper;

import com.andersen.products_app.entity.Category;
import com.andersen.products_app.entity.Product;
import com.andersen.products_app.model.request.ProductCreationRequest;
import com.andersen.products_app.model.response.ProductResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

  @Mapping(target = "name", source = " productCreationRequest.name")
  @Mapping(target = "category", source = "category")
  Product toProductEntity(ProductCreationRequest productCreationRequest, String logo, Category category);

  @Mapping(source = "category.name", target = "categoryName")
  ProductResponse toProductResponse(Product product);
}
