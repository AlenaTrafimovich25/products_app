package com.andersen.products_app.unit_tests.service.impl;

import static com.andersen.utils.Constants.CATEGORY_ID;
import static com.andersen.utils.Constants.CATEGORY_NAME;
import static com.andersen.utils.Constants.FILE_NAME;
import static com.andersen.utils.Constants.LOGO;
import static com.andersen.utils.Constants.PAGE_NUMBER;
import static com.andersen.utils.Constants.PAGE_SIZE;
import static com.andersen.utils.Constants.PRODUCT_ID;
import static com.andersen.utils.Constants.PRODUCT_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.andersen.products_app.entity.Category;
import com.andersen.products_app.entity.Product;
import com.andersen.products_app.mapper.CategoryMapper;
import com.andersen.products_app.mapper.CategoryMapperImpl;
import com.andersen.products_app.mapper.ProductMapper;
import com.andersen.products_app.mapper.ProductMapperImpl;
import com.andersen.products_app.model.request.CategoryCreationRequest;
import com.andersen.products_app.model.request.ProductCreationRequest;
import com.andersen.products_app.model.request.ProductUpdateRequest;
import com.andersen.products_app.model.response.CategoryResponse;
import com.andersen.products_app.model.response.ProductResponse;
import com.andersen.products_app.service.CategoryService;
import com.andersen.products_app.service.ProductService;
import com.andersen.products_app.service.S3Service;
import com.andersen.products_app.service.impl.ProductsFacade;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class ProductsFacadeTest {
  @InjectMocks
  private ProductsFacade productsFacade;

  @Mock
  private ProductService productService;

  @Spy
  private ProductMapper productMapper = new ProductMapperImpl();

  @Mock
  private S3Service s3service;

  @Mock
  private CategoryService categoryService;

  @Spy
  private CategoryMapper categoryMapper = new CategoryMapperImpl();

  private Pageable pageable;
  private Product product;
  private Page<String> productNamesPage;
  private Page<Product> productsPage;
  private Page<Category> categoryPage;
  private Page<CategoryResponse> categoryResponsePage;
  private MultipartFile multipartFile;
  @Captor
  private ArgumentCaptor<Product> productEntityArgumentCaptor;
  private Category category;
  private Page<ProductResponse> productResponsePage;

  @BeforeEach
  void setUp() {
    pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);

    category = new Category();
    category.setName(CATEGORY_NAME);
    category.setId(CATEGORY_ID);
    categoryPage = new PageImpl<>(List.of(category), pageable, pageable.getPageSize());

    product =
        new Product(PRODUCT_ID, PRODUCT_NAME, LOGO, category);
    productsPage = new PageImpl<>(List.of(product), pageable, pageable.getPageSize());
    productNamesPage = new PageImpl<>(List.of(PRODUCT_NAME), pageable, pageable.getPageSize());

    var productResponse =
        new ProductResponse(PRODUCT_ID, PRODUCT_NAME, LOGO, CATEGORY_NAME);
    productResponsePage =
        new PageImpl<>(List.of(productResponse), pageable, pageable.getPageSize());

    var categoryResponse = new CategoryResponse(CATEGORY_ID, CATEGORY_NAME,
        null);
    categoryResponsePage =
        new PageImpl<>(List.of(categoryResponse), pageable, pageable.getPageSize());

    multipartFile = new MockMultipartFile(FILE_NAME, new byte[1]);
  }

  @Test
  void whenGetAllProducts_theProductResponseReturned() {
    when(productService.getProducts(any())).thenReturn(productsPage);

    productsFacade.getProducts(null, null, pageable);

    verify(productService).getProducts(pageable);
  }

  @Test
  void whenGetProductsByCategoriesNames_theProductResponseReturned() {
    when(productService.getProductsByCategoriesName(any(), any())).thenReturn(productsPage);

    productsFacade.getProducts(Set.of(CATEGORY_NAME), null, pageable);

    verify(productService).getProductsByCategoriesName(Set.of(CATEGORY_NAME), pageable);
  }

  @Test
  void whenGetProductsByName_theProductResponseReturned() {
    when(productService.getProductsByProductName(any(), any())).thenReturn(productsPage);

    productsFacade.getProducts(null, PRODUCT_NAME, pageable);

    verify(productService).getProductsByProductName(PRODUCT_NAME, pageable);
  }

  @Test
  void whenGetDistinctProductName_theProductResponseReturned() {
    when(productService.getDistinctProductNames(any())).thenReturn(productNamesPage);

    productsFacade.getDistinctProductNames(pageable);

    verify(productService).getDistinctProductNames(pageable);
  }

  @Test
  void whenGetProduct_theProductResponseReturned() {
    when(productService.getProduct(any())).thenReturn(product);

    productsFacade.getProduct(PRODUCT_ID);

    verify(productService).getProduct(PRODUCT_ID);
  }

  @Test
  void whenUpdateProduct_theProductUpdated() {
    var existingProduct =
        new Product(PRODUCT_ID, "previousName", "previousLogo", category);
    when(productService.getProduct(any())).thenReturn(existingProduct);
    var productUpdateRequest = new ProductUpdateRequest(PRODUCT_NAME);
    when(s3service.putS3Object(any(), any())).thenReturn(LOGO);

    var expectedProductEntity = new Product();
    expectedProductEntity.setName(PRODUCT_NAME);
    expectedProductEntity.setLogo(LOGO);

    productsFacade.updateProduct(PRODUCT_ID, productUpdateRequest, multipartFile);

    verify(productService).getProduct(PRODUCT_ID);
    verify(s3service).deleteFile("previousLogo");
    verify(s3service).putS3Object(any(), refEq(multipartFile));
    verify(productService).saveProduct(productEntityArgumentCaptor.capture());
    assertEquals(expectedProductEntity.getName(), productEntityArgumentCaptor.getValue().getName());
    assertEquals(expectedProductEntity.getLogo(), productEntityArgumentCaptor.getValue().getLogo());
  }

  @Test
  void whenCreateProduct_theProductAdded() {
    when(categoryService.getCategory(any())).thenReturn(category);

    var productCreationRequest = new ProductCreationRequest(CATEGORY_ID, PRODUCT_NAME);
    when(s3service.putS3Object(any(), any())).thenReturn(LOGO);

    var expectedProductEntity = new Product();
    expectedProductEntity.setName(PRODUCT_NAME);
    expectedProductEntity.setLogo(LOGO);
    expectedProductEntity.setCategory(category);

    productsFacade.createProduct(productCreationRequest, multipartFile);

    verify(categoryService).getCategory(CATEGORY_ID);
    verify(s3service).putS3Object(any(), refEq(multipartFile));
    verify(productMapper).toProductEntity(productCreationRequest, LOGO, category);
    verify(productService).saveProduct(productEntityArgumentCaptor.capture());
    var productCaptureValue = productEntityArgumentCaptor.getValue();
    assertEquals(expectedProductEntity.getLogo(), productCaptureValue.getLogo());
    assertEquals(expectedProductEntity.getName(), productCaptureValue.getName());
    assertEquals(expectedProductEntity.getCategory().getId(),
        productCaptureValue.getCategory().getId());
  }

  @Test
  void whenDeleteProduct_theProductDeleted() {
    when(productService.getProduct(any())).thenReturn(product);

    productsFacade.deleteProduct(PRODUCT_ID);

    verify(productService).getProduct(PRODUCT_ID);
    verify(productService).deleteProduct(product);
    verify(s3service).deleteFile(product.getLogo());
  }

  @Test
  void whenGetCategory_theReturnCategoryResponse() {
    category.setProducts(Set.of(product));
    when(categoryService.getCategory(any())).thenReturn(category);

    var expectedCategoryResponse = new CategoryResponse(CATEGORY_ID, CATEGORY_NAME,
        productResponsePage);

    var actualResponse = productsFacade.getCategory(CATEGORY_ID, pageable);

    verify(categoryService).getCategory(CATEGORY_ID);
    verify(productMapper).toProductResponse(product);
    assertEquals(expectedCategoryResponse, actualResponse);
  }

  @Test
  void whenGetCategories_theReturnCategoryResponse() {
    when(categoryService.getCategories(any())).thenReturn(categoryPage);

    var actualResponse = productsFacade.getCategories(pageable);

    verify(categoryService).getCategories(pageable);
    verify(categoryMapper).toCategoryResponse(category);
    assertEquals(categoryResponsePage.getContent(), actualResponse.getContent());
  }

  @Test
  void whenCreateCategory_theCategoryAdded() {
    var categoryCreationRequest = new CategoryCreationRequest(CATEGORY_NAME);

    productsFacade.createCategory(categoryCreationRequest);

    verify(categoryMapper).toCategoryEntity(categoryCreationRequest);
    verify(categoryService).saveCategory(refEq(category, "id", "products"));
  }

  @Test
  void whenDeleteCategory_theCategoryDeleted() {
    category.setProducts(Set.of(product));
    when(categoryService.getCategory(any())).thenReturn(category);

    productsFacade.deleteCategory(CATEGORY_ID);

    verify(categoryService).deleteCategory(CATEGORY_ID);
    verify(productService).deleteProduct(product);
  }
}
