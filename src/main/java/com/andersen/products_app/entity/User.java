package com.andersen.products_app.entity;

import com.andersen.products_app.listener.UserListener;
import com.andersen.products_app.model.enums.Role;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

@Entity
@Table(name = "users")
@EntityListeners(UserListener.class)
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String firstName;

  private String lastName;

  private String email;

  private String password;

  @Enumerated(EnumType.STRING)
  @JdbcType(PostgreSQLEnumJdbcType.class)
  @Column(name = "role", columnDefinition = "role")
  @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
  @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
  private Set<Role> roles = new HashSet<>();

  public void addRole(Role role) {
    if (this.roles == null) {
      this.roles = new HashSet<>();
    }

    this.roles.add(role);
  }

  public Long getId() {
    return id;
  }

  public String getEmail() {
    return email;
  }

  public String getPassword() {
    return password;
  }

  public Set<Role> getRoles() {
    return roles;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setRoles(Set<Role> roles) {
    this.roles = roles;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    User that = (User) o;
    return Objects.equals(id, that.id) &&
           Objects.equals(firstName, that.firstName) &&
           Objects.equals(lastName, that.lastName) &&
           Objects.equals(email, that.email) &&
           Objects.equals(password, that.password) &&
           Objects.equals(roles, that.roles);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, firstName, lastName, email, password, roles);
  }
}
