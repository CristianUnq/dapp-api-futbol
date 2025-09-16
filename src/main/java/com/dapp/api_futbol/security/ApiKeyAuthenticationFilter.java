package com.dapp.api_futbol.security;

import com.dapp.api_futbol.model.ApiKey;
import com.dapp.api_futbol.service.ApiKeyService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private final ApiKeyService apiKeyService;

    public ApiKeyAuthenticationFilter(ApiKeyService apiKeyService) {
        this.apiKeyService = apiKeyService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("X-API-KEY");
        if (header != null && !header.isBlank()) {
            try {
                java.util.Optional<ApiKey> found = apiKeyService.findByRawKey(header);
                if (found.isPresent()) {
                    SecurityContextHolder.getContext().setAuthentication(new ApiKeyAuthentication(found.get()));
                } else {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid API Key");
                    return;
                }
            } catch (Exception ex) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid API Key");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
