package com.dapp.api_futbol.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal;

    public JwtAuthenticationToken(Object principal) {
        super(AuthorityUtils.NO_AUTHORITIES);
        this.principal = principal;
        setAuthenticated(true);
    }

    /**
     * Lightweight Authentication used after validating a JWT. It stores the
     * subject (username) as the principal. No authorities are included by
     * default in this simple implementation.
     */

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}
