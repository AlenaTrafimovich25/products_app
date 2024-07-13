package com.andersen.products_app.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "categories")
public class Category {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  @OneToMany(mappedBy = "category")
  private Set<Product> products;

  public Category() {
  }

  public Category(Long id, String name, Set<Product> products) {
    this.id = id;
    this.name = name;
    this.products = products;
  }

  public Category(Long id, String name) {
    this.id = id;
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Long getId() {
    return id;
  }

  public Set<Product> getProducts() {
    return products;
  }

  public void setProducts(Set<Product> products) {
    this.products = products;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Category that = (Category) o;
    return Objects.equals(id, that.id) && Objects.equals(name, that.name) &&
           Objects.equals(products, that.products);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, products);
  }
}
