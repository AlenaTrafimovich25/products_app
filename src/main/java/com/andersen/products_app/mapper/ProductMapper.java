package com.andersen.products_app.mapper;

import com.andersen.products_app.entity.Product;
import com.andersen.products_app.model.request.ProductCreationRequest;
import com.andersen.products_app.model.response.ProductResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "name", source = " productCreationRequest.name")
  @Mapping(target = "category.id", source = " productCreationRequest.categoryId")
  Product toProductEntity(ProductCreationRequest productCreationRequest, String logo);

  @Mapping(source = "category.name", target = "categoryName")
  ProductResponse toProductResponse(Product product);
}
