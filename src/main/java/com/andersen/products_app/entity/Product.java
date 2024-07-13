package com.andersen.products_app.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "products")
public class Product {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  private String logo;

  @ManyToOne
  @JoinColumn(name = "category_id")
  private Category category;

  public Product(Long id, String name, String logo, Category category) {
    this.id = id;
    this.name = name;
    this.logo = logo;
    this.category = category;
  }

  public Product() {
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getLogo() {
    return logo;
  }

  public void setLogo(String logo) {
    this.logo = logo;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Product that = (Product) o;
    return Objects.equals(id, that.id) && Objects.equals(name, that.name) &&
           Objects.equals(logo, that.logo) &&
           Objects.equals(category, that.category);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, logo, category);
  }

  public Category getCategory() {
    return category;
  }

  public void setCategory(Category category) {
    this.category = category;
  }
}
