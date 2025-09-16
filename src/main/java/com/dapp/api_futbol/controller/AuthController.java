package com.dapp.api_futbol.controller;

import com.dapp.api_futbol.model.ApiKey;
import com.dapp.api_futbol.model.User;
import com.dapp.api_futbol.security.JwtTokenProvider;
import com.dapp.api_futbol.service.ApiKeyService;
import com.dapp.api_futbol.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final ApiKeyService apiKeyService;

    public AuthController(UserService userService, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider, ApiKeyService apiKeyService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.apiKeyService = apiKeyService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String,String> body) {
        String username = body.get("username");
        String password = body.get("password");
        User u = userService.register(username, password);
        return ResponseEntity.ok(Map.of("id", u.getId(), "username", u.getUsername()));
    }

    @PostMapping("/apikeys")
    public ResponseEntity<?> createApiKey(@RequestBody Map<String,String> body) {
        String username = body.get("username");
        String name = body.getOrDefault("name","default");
        User u = userService.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        String raw = apiKeyService.createForUser(u, name);
        // Return raw key once (user must copy it)
        return ResponseEntity.ok(Map.of("apiKey", raw));
    }
}
