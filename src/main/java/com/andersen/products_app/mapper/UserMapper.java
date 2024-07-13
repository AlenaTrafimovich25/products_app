package com.andersen.products_app.mapper;

import com.andersen.products_app.entity.User;
import com.andersen.products_app.model.request.RegisterUserRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

  @Mapping(target = "id", ignore = true)
  User toUserEntity(RegisterUserRequest registerUserRequest);
}
