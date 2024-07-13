package com.andersen.products_app.model;

import com.andersen.products_app.entity.User;
import com.andersen.products_app.model.enums.Role;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserAuthDetails implements UserDetails {

  private final String email;
  private final String password;
  private final List<SimpleGrantedAuthority> authorities;

  public UserAuthDetails(User user) {
    this.email = user.getEmail();
    this.password = user.getPassword();
    this.authorities = user.getRoles().stream()
        .map(Role::name)
        .map(SimpleGrantedAuthority::new)
        .toList();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return email;
  }
}
