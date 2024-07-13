package com.andersen.products_app.model;

import com.andersen.products_app.entity.User;
import com.andersen.products_app.model.enums.Role;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public record UserAuthDetails(String email, String password,
                              List<SimpleGrantedAuthority> authorities)
    implements UserDetails {

  public UserAuthDetails(User user) {
    this(user.getEmail(),
        user.getPassword(),
        user.getRoles().stream()
            .map(Role::name)
            .map(SimpleGrantedAuthority::new)
            .toList());
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
