package com.kensbunker.mn.auth.jwt;

import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.reactivex.rxjava3.annotations.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public class UserDetails implements AuthenticationResponse {
  private String username;
  private Collection<String> roles;
  private Map<String, Object> attributes;

  public UserDetails(String username, Collection<String> roles) {
    this(username, roles, (Map) null);
  }

  public UserDetails(String username, Collection<String> roles, Map<String, Object> attributes) {
    if (username != null && roles != null) {
      this.username = username;
      this.roles = roles;
      this.attributes = attributes;
    } else {
      throw new IllegalArgumentException(
          "Cannot construct a UserDetails with a null username or authorities");
    }
  }

  @Override
  public boolean isAuthenticated() {
    return AuthenticationResponse.super.isAuthenticated();
  }

  @Override
  public Optional<Authentication> getAuthentication() {
    return Optional.empty();
  }

  @Override
  public Optional<String> getMessage() {
    return AuthenticationResponse.super.getMessage();
  }
}
