package com.andersen.products_app.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterUserRequest(
    @NotBlank
    String firstName,
    @NotBlank
    String lastName,
    @NotBlank
    @Email
    String email,
    @NotBlank
    String password
) {
}
