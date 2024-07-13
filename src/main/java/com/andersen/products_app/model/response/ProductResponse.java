package com.andersen.products_app.model.response;


public record ProductResponse(
    Long id,
    String name,
    String logo,
    String categoryName) {
}
