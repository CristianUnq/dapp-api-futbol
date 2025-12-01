package com.dapp.api_futbol.config;

import com.dapp.api_futbol.security.ApiKeyAuthenticationFilter;
import com.dapp.api_futbol.security.JwtAuthenticationFilter;
import com.dapp.api_futbol.security.JwtTokenProvider;
import com.dapp.api_futbol.service.ApiKeyService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.http.SessionCreationPolicy;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class SecurityConfig {

    private final JwtTokenProvider tokenProvider;
    private final ApiKeyService apiKeyService;

    public SecurityConfig(JwtTokenProvider tokenProvider, ApiKeyService apiKeyService) {
        this.tokenProvider = tokenProvider;
        this.apiKeyService = apiKeyService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @ConditionalOnMissingBean(SecurityFilterChain.class)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // ApiKey filter should run before JWT filter so that API key auth is attempted first
        // Notes:
        // - API should be stateless: configure SessionCreationPolicy.STATELESS
        // - ApiKey filter runs before JWT filter so both mechanisms are supported.
        http
            .addFilterBefore(new ApiKeyAuthenticationFilter(apiKeyService), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(new JwtAuthenticationFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/swagger-ui/index.html",
                    "/v3/api-docs.yaml",
                    "/auth/register",
                    "/auth/login",
                    "/h2-console/**"
                ).permitAll()
                .anyRequest().authenticated()
            )
            // Return 401 Unauthorized for unauthenticated requests (REST APIs commonly use 401)
            .exceptionHandling(e -> e.authenticationEntryPoint((request, response, authException) ->
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED)))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .headers(headers -> headers
                .contentTypeOptions(opts -> opts.disable())
                .xssProtection(xss -> xss.disable())
                .contentSecurityPolicy(csp -> csp.policyDirectives("frame-ancestors 'self'")));

        return http.build();
    }
}
