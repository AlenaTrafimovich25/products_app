package com.andersen.products_app.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.domain.Page;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CategoryResponse(
    Long id,
    String name,
    Page<ProductResponse> productEntities
) {
}
