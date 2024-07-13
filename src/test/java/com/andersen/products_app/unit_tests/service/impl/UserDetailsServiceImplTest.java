package com.andersen.products_app.unit_tests.service.impl;

import static com.andersen.products_app.model.enums.Role.USER;
import static com.andersen.utils.Constants.EMAIL;
import static com.andersen.utils.Constants.PASSWORD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.andersen.products_app.entity.User;
import com.andersen.products_app.exception.EmailNotFoundException;
import com.andersen.products_app.repository.UserRepository;
import com.andersen.products_app.service.impl.UserDetailsServiceImpl;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {
  @Mock
  private UserRepository userRepository;
  @InjectMocks
  private UserDetailsServiceImpl userDetailsService;

  @Test
  void whenLoadUserByUsername_thenSuccess() {
    var userEntity = new User();
    userEntity.setEmail(EMAIL);
    userEntity.setPassword(PASSWORD);
    userEntity.addRole(USER);

    when(userRepository.findByEmail(any())).thenReturn(Optional.of(userEntity));

    var userDetails = userDetailsService.loadUserByUsername(EMAIL);

    assertEquals(userEntity.getEmail(), userDetails.getUsername());
    assertEquals(userEntity.getPassword(), userDetails.getPassword());
    assertFalse(userDetails.getAuthorities().isEmpty());
  }

  @Test
  void whenLoadUserByUsernameAndUserNotFound_thenExceptionThrown() {
    when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

    assertThrows(EmailNotFoundException.class,
        () -> userDetailsService.loadUserByUsername(EMAIL));
  }

  @Test
  void whenAddUser_thenSuccess() {
    var userEntity = new User();
    userEntity.setPassword(PASSWORD);

    userDetailsService.addUser(userEntity);

    verify(userRepository).save(userEntity);
  }
}
