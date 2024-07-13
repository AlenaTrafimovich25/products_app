package com.andersen.products_app.config.security;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.andersen.products_app.service.impl.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
  private final JwtService jwtService;
  private final UserDetailsService userDetailsService;

  public JwtAuthFilter(JwtService jwtService, UserDetailsService userDetailsService) {
    this.jwtService = jwtService;
    this.userDetailsService = userDetailsService;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                  FilterChain filterChain)
      throws ServletException, IOException {
    var authHeader = request.getHeader(AUTHORIZATION);
    String token = null;
    String email = null;
    if (!StringUtils.isEmpty(authHeader)
        && authHeader.startsWith("Bearer ")) {
      token = authHeader.substring(7);
      email = jwtService.extractEmail(token);
    }

    if (!StringUtils.isEmpty(email)
        && SecurityContextHolder.getContext().getAuthentication() == null) {
      var userDetails = userDetailsService.loadUserByUsername(email);
      if (jwtService.validateToken(token)) {
        var authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null,
            userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
      }
    }
    filterChain.doFilter(request, response);
  }
}
