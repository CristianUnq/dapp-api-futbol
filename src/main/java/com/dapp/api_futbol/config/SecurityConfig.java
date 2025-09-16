package com.dapp.api_futbol.config;

import com.dapp.api_futbol.security.ApiKeyAuthenticationFilter;
import com.dapp.api_futbol.security.JwtAuthenticationFilter;
import com.dapp.api_futbol.security.JwtTokenProvider;
import com.dapp.api_futbol.service.ApiKeyService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // ApiKey filter should run before JWT filter so that API key auth is attempted first
        http
            .addFilterBefore(new ApiKeyAuthenticationFilter(apiKeyService), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(new JwtAuthenticationFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/swagger-ui/index.html",
                    "/swagger-ui/**",
                    "/v3/api-docs.yaml",
                    "/auth/**"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .csrf(csrf -> csrf.disable())
            .formLogin();

        return http.build();
    }
}
