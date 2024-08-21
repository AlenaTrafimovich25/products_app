package com.andersen.products_app.config.security;

import static com.andersen.products_app.model.enums.Role.EDITOR;
import static com.andersen.products_app.model.enums.Role.USER;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Value("${spring.security.public-urls}")
  private String[] publicUrls;

  @Value("${spring.security.private-urls}")
  private String[] privateUrls;

  @Bean
  PasswordEncoder encoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  AuthenticationManager authenticationManager(
      AuthenticationConfiguration authenticationConfiguration)
      throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }

  @Bean
  AuthenticationProvider daoAuthenticationProvider(UserDetailsService userDetailsService,
                                                   PasswordEncoder encoder) {
    var daoAuthenticationProvider = new DaoAuthenticationProvider();
    daoAuthenticationProvider.setUserDetailsService(userDetailsService);
    daoAuthenticationProvider.setPasswordEncoder(encoder);
    return daoAuthenticationProvider;
  }

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http,
                                          JwtAuthFilter jwtAuthFilter)
      throws Exception {
    return http
        .httpBasic(Customizer.withDefaults())
        .csrf(AbstractHttpConfigurer::disable)
        .cors(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(req -> req
            .requestMatchers(publicUrls)
            .permitAll()
            .requestMatchers(GET, privateUrls)
            .hasAnyAuthority(USER.name(), EDITOR.name())
            .requestMatchers(POST, privateUrls)
            .hasAnyAuthority(USER.name(), EDITOR.name())
            .requestMatchers(DELETE, privateUrls)
            .hasAnyAuthority(USER.name(), EDITOR.name())
            .requestMatchers(PUT, privateUrls)
            .hasAuthority(EDITOR.name())
            .anyRequest().authenticated()
        )
        .sessionManagement(
            session -> session.sessionCreationPolicy((SessionCreationPolicy.STATELESS)))
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
  }
}
