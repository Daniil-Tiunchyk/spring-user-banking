package com.example.spring_user_banking.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;

public class SimpleAuthToken implements Authentication {
    private final Long userId;

    public SimpleAuthToken(Long userId) {
        this.userId = userId;
    }

    @Override public Long getPrincipal() { return userId; }
    @Override public boolean isAuthenticated() { return true; }
    @Override public void setAuthenticated(boolean isAuthenticated) {}
    @Override public String getName() { return userId.toString(); }
    @Override public Object getCredentials() { return null; }
    @Override public Object getDetails() { return null; }
    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return Collections.emptyList(); }
}