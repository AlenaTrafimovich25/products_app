package com.andersen.products_app.unit_tests.controller;

import static com.andersen.utils.Constants.CATEGORY_ID;
import static com.andersen.utils.Constants.CATEGORY_NAME;
import static com.andersen.utils.Constants.PAGE_NUMBER;
import static com.andersen.utils.Constants.PAGE_SIZE;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersen.products_app.config.security.JwtAuthFilter;
import com.andersen.products_app.controller.CategoryController;
import com.andersen.products_app.model.request.CategoryCreationRequest;
import com.andersen.products_app.service.impl.ProductsFacade;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

  private static final String BASE_URL = "/v1/categories";
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private WebApplicationContext webApplicationContext;
  @MockBean
  private ProductsFacade productsFacade;
  @MockBean
  private JwtAuthFilter jwtAuthFilter;
  @MockBean
  private UserDetailsService userDetailsService;
  @Spy
  private ObjectMapper objectMapper;
  private final Pageable pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
  }

  @Test
  void whenGetCategory_thenOk() throws Exception {
    mockMvc.perform(get(BASE_URL + "/" + CATEGORY_ID)
            .param("page", String.valueOf(PAGE_NUMBER))
            .param("size", String.valueOf(PAGE_SIZE)))
        .andExpect(status().isOk());

    verify(productsFacade).getCategory(CATEGORY_ID, pageable);
  }

  @Test
  void whenGetCategories_thenOk() throws Exception {
    mockMvc.perform(get(BASE_URL)
            .param("page", String.valueOf(PAGE_NUMBER))
            .param("size", String.valueOf(PAGE_SIZE)))
        .andExpect(status().isOk());

    verify(productsFacade).getCategories(pageable);
  }

  @Test
  void whenAddCategory_thenOk() throws Exception {
    var categoryCreationRequest = new CategoryCreationRequest(CATEGORY_NAME);

    mockMvc.perform(post(BASE_URL)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsBytes(categoryCreationRequest)))
        .andExpect(status().isCreated());

    verify(productsFacade).createCategory(categoryCreationRequest);
  }

  @Test
  void whenAddCategory_thenBadRequest() throws Exception {
//    var categoryCreationRequest = new CategoryCreationRequest(CATEGORY_NAME);

    mockMvc.perform(post(BASE_URL)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isBadRequest());

//    verify(productsFacade).createCategory(categoryCreationRequest);
  }

  @Test
  void whenDeleteCategory_thenNoContent() throws Exception {
    mockMvc.perform(delete(BASE_URL + "/" + CATEGORY_ID)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isNoContent());

    verify(productsFacade).deleteCategory(CATEGORY_ID);
  }
}
