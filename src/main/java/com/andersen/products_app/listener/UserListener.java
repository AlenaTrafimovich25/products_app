package com.andersen.products_app.listener;

import com.andersen.products_app.entity.User;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserListener {
  private final PasswordEncoder encoder;

  public UserListener() {
    this.encoder = new BCryptPasswordEncoder();
  }

  @PrePersist
  @PreUpdate
  private void beforeSave(User user) {
    var password = user.getPassword();
    user.setPassword(encoder.encode(password));
  }
}
