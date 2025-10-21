package com.dapp.api_futbol.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        // If an Authorization header with a Bearer token is present, validate it
        // and set an Authentication with the token's subject as principal.
        // We keep the Authentication simple (no authorities) because this example
        // focuses on identification rather than role-based access control.
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                var jws = tokenProvider.validateToken(token);
                String username = jws.getBody().getSubject();
                if (username != null) {
                    // Store a lightweight Authentication object containing the username
                    SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(username));
                } else {
                    // Missing subject -> invalid token
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token: missing subject");
                    return;
                }
            } catch (Exception ex) {
                // Token invalid or expired
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
