package com.andersen.products_app.service.impl;

import com.andersen.products_app.entity.User;
import com.andersen.products_app.exception.EmailNotFoundException;
import com.andersen.products_app.model.UserAuthDetails;
import com.andersen.products_app.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
  private final UserRepository userRepository;

  public UserDetailsServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String email) {
    var userDetails = userRepository.findByEmail(email);
    return userDetails.map(UserAuthDetails::new)
        .orElseThrow(() -> new EmailNotFoundException(email));
  }

  public void createUser(User user) {
    userRepository.save(user);
  }

  public boolean existsUser(String email) {
    return userRepository.existsByEmail(email);
  }
}
