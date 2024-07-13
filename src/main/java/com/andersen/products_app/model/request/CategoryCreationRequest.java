package com.andersen.products_app.model.request;

import jakarta.validation.constraints.NotBlank;

public record CategoryCreationRequest(
    @NotBlank
    String name) {
}
