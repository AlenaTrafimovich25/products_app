package com.andersen.products_app.aspect;

import com.andersen.products_app.entity.Product;
import com.andersen.products_app.service.CacheService;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class CacheAspect {
  private final CacheService cacheService;

  public CacheAspect(CacheService cacheService) {
    this.cacheService = cacheService;
  }

  @Around(
      value = """
          execution(* com.andersen.products_app.repository.ProductRepository
          .findAll(org.springframework.data.domain.Pageable))
          """)
  public Object getProducts(ProceedingJoinPoint joinPoint) {
    var pageable = (Pageable) joinPoint.getArgs()[0];
    return getPage(cacheService.getAll(), pageable);
  }

  @Around(
      value = """
          execution(* com.andersen.products_app.repository.ProductRepository
          .findProductsByCategoryNameIgnoreCaseIn(..))
          """)
  public Object getProductsByCategoriesName(ProceedingJoinPoint joinPoint) {
    Set<String> categories = (Set<String>) joinPoint.getArgs()[0];
    var pageable = (Pageable) joinPoint.getArgs()[1];
    List<Product> products = cacheService.getAll()
        .stream()
        .filter(categoryFilter(categories))
        .toList();

    return getPage(products, pageable);
  }

  @Around(
      value = """
          execution(* com.andersen.products_app.repository.ProductRepository
          .findProductsByNameContainingIgnoreCase(..))
          """)
  public Object getProductsByProductName(ProceedingJoinPoint joinPoint) {
    var productName = (String) joinPoint.getArgs()[0];
    var pageable = (Pageable) joinPoint.getArgs()[1];
    List<Product> products = cacheService.getAll()
        .stream()
        .filter(productNameFilter(productName))
        .toList();

    return getPage(products, pageable);
  }

  @Around(value = "execution(* com.andersen.products_app.repository.ProductRepository.findById(..))")
  public Object getProductById(ProceedingJoinPoint joinPoint) {
    var entityId = (Long) joinPoint.getArgs()[0];
    return cacheService.get(entityId);
  }

  @Around(value = "execution(* com.andersen.products_app.repository.ProductRepository.findDistinctNames(..)) ")
  public Object getProductsNames(ProceedingJoinPoint joinPoint) {
    var pageable = (Pageable) joinPoint.getArgs()[0];
    List<String> names = cacheService.getAll()
        .stream()
        .map(Product::getName)
        .distinct()
        .toList();
    return getPage(names, pageable);
  }

  @Around(value = "execution(* com.andersen.products_app.repository.ProductRepository.save(..))")
  public Object saveProduct(ProceedingJoinPoint joinPoint) throws Throwable {
    var productEntity = (Product) joinPoint.proceed();
    cacheService.put(productEntity);
    return productEntity;
  }

  @Around(value = "execution(* com.andersen.products_app.repository.ProductRepository.delete(..))")
  public Object deleteProduct(ProceedingJoinPoint joinPoint) throws Throwable {
    var productEntity = (Product) joinPoint.proceed();
    cacheService.evict(productEntity);
    return productEntity;
  }

  private <T> Page<T> getPage(List<T> filteredItems, Pageable pageable) {
    return new PageImpl<>(filteredItems, pageable, pageable.getPageSize());
  }

  private Predicate<Product> categoryFilter(Set<String> categories) {
    Set<String> lowerCaseCategories = categories.stream()
        .map(String::toLowerCase)
        .collect(Collectors.toSet());

    return product -> lowerCaseCategories
        .contains(product.getCategory().getName().toLowerCase());
  }

  private Predicate<Product> productNameFilter(String name) {
    return product -> product.getName().toLowerCase().contains(name.toLowerCase());
  }
}
