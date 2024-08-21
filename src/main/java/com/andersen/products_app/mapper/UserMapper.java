package com.andersen.products_app.mapper;

import com.andersen.products_app.entity.User;
import com.andersen.products_app.model.request.RegisterUserRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

  User toUser(RegisterUserRequest registerUserRequest);
}
