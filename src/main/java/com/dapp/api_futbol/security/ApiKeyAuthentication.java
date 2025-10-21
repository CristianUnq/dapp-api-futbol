package com.dapp.api_futbol.security;

import com.dapp.api_futbol.model.ApiKey;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class ApiKeyAuthentication implements Authentication {

    private final ApiKey apiKey;
    private boolean authenticated = true;

    public ApiKeyAuthentication(ApiKey apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * This Authentication implementation wraps an ApiKey entity. It provides
     * the associated username as principal and the key fingerprint as credentials.
     * It intentionally does not grant authorities here; you can extend it later
     * if you need role/permission checks.
     */

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return java.util.Collections.emptyList();
    }

    @Override
    public Object getCredentials() {
        return apiKey.getKeyHash();
    }

    @Override
    public Object getDetails() {
        return apiKey;
    }

    @Override
    public Object getPrincipal() {
        return apiKey.getUser().getUsername();
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;
    }

    @Override
    public String getName() {
        return apiKey.getName();
    }
}
