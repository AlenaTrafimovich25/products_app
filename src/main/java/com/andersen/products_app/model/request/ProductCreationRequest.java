package com.andersen.products_app.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductCreationRequest(
    @NotNull
    Long categoryId,
    @NotBlank
    String name
) {
}
