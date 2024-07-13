package com.andersen.products_app.unit_tests.controller;

import static com.andersen.utils.Constants.CATEGORY_ID;
import static com.andersen.utils.Constants.CATEGORY_NAME;
import static com.andersen.utils.Constants.PAGE_NUMBER;
import static com.andersen.utils.Constants.PAGE_SIZE;
import static com.andersen.utils.Constants.PRODUCT_ID;
import static com.andersen.utils.Constants.PRODUCT_NAME;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersen.products_app.config.security.JwtAuthFilter;
import com.andersen.products_app.controller.ProductController;
import com.andersen.products_app.model.request.ProductCreationRequest;
import com.andersen.products_app.model.request.ProductUpdateRequest;
import com.andersen.products_app.service.impl.ProductsFacade;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc
class ProductControllerTest {
  private static final String BASE_URL = "/v1/products";

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

  private MockMultipartFile photo;
  private MockMultipartFile request;

  private final Pageable pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);


  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    photo = new MockMultipartFile("file", "photo.jpg", MediaType.IMAGE_JPEG_VALUE, new byte[1]);
  }

  @Test
  void whenGetProduct_thenSuccess() throws Exception {
    mockMvc.perform(get(BASE_URL + "/" + PRODUCT_ID))
        .andExpect(status().isOk());

    verify(productsFacade).getProduct(PRODUCT_ID);
  }

  @Test
  void whenGetProductsWithCategories_thenSuccess() throws Exception {
    mockMvc.perform(get(BASE_URL)
            .param("categories", CATEGORY_NAME)
            .param("page", String.valueOf(PAGE_NUMBER))
            .param("size", String.valueOf(PAGE_SIZE)))
        .andExpect(status().isOk());

    verify(productsFacade).getProducts(Set.of(CATEGORY_NAME), null, pageable);
  }

  @Test
  void whenGetProductsWithName_thenSuccess() throws Exception {
    mockMvc.perform(get(BASE_URL)
            .param("name", PRODUCT_NAME)
            .param("page", String.valueOf(PAGE_NUMBER))
            .param("size", String.valueOf(PAGE_SIZE)))
        .andExpect(status().isOk());

    verify(productsFacade).getProducts(null, PRODUCT_NAME, pageable);
  }

  @Test
  void whenGetProducts_thenSuccess() throws Exception {
    mockMvc.perform(get(BASE_URL)
            .param("page", String.valueOf(PAGE_NUMBER))
            .param("size", String.valueOf(PAGE_SIZE)))
        .andExpect(status().isOk());

    verify(productsFacade).getProducts(null, null, pageable);
  }

  @Test
  void whenGetDistinctProductNames_thenSuccess() throws Exception {
    mockMvc.perform(get(BASE_URL + "/names")
            .param("page", String.valueOf(PAGE_NUMBER))
            .param("size", String.valueOf(PAGE_SIZE)))
        .andExpect(status().isOk());

    verify(productsFacade).getDistinctProductNames(pageable);
  }

  @Test
  void whenAddProduct_thenSuccess() throws Exception {
    var productCreationRequest = new ProductCreationRequest(CATEGORY_ID, CATEGORY_NAME);
    request = new MockMultipartFile("productCreationRequest", "foo.json",
        MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(productCreationRequest));

    mockMvc.perform(multipart(BASE_URL)
            .file(photo)
            .file(request))
        .andExpect(status().isCreated());

    verify(productsFacade).createProduct(productCreationRequest, photo);
  }

  @Test
  void whenUpdateProduct_thenSuccess() throws Exception {
    var productUpdateRequest = new ProductUpdateRequest(PRODUCT_NAME);
    request = new MockMultipartFile("productUpdateRequest", "foo.json",
        MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(productUpdateRequest));

    mockMvc.perform(MockMvcRequestBuilders
            .multipart(HttpMethod.PUT, BASE_URL + "/" + PRODUCT_ID)
            .file(photo)
            .file(request))
        .andExpect(status().isNoContent());

    verify(productsFacade).updateProduct(PRODUCT_ID, productUpdateRequest, photo);
  }

  @Test
  void whenDeleteProduct_thenNoContent() throws Exception {
    mockMvc.perform(delete(BASE_URL + "/" + PRODUCT_ID))
        .andExpect(status().isNoContent());

    verify(productsFacade).deleteProduct(PRODUCT_ID);
  }
}
