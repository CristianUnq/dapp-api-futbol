package com.dapp.api_futbol.controller;

import com.dapp.api_futbol.model.ApiKey;
import com.dapp.api_futbol.model.User;
import com.dapp.api_futbol.security.JwtTokenProvider;
import com.dapp.api_futbol.service.ApiKeyService;
import com.dapp.api_futbol.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

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
        logger.info("Register request for username: {}", body.get("username"));
        String username = body.get("username");
        String password = body.get("password");
        User u = userService.register(username, password);
        return ResponseEntity.ok(Map.of("id", u.getId(), "username", u.getUsername()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String,String> body) {
        logger.info("Login request for username: {}", body.get("username"));
        String username = body.get("username");
        String password = body.get("password");
        var opt = userService.findByUsername(username);
        if (opt.isEmpty()) return ResponseEntity.status(401).body(Map.of("error","Invalid credentials"));
        User u = opt.get();
        if (!passwordEncoder.matches(password, u.getPassword())) return ResponseEntity.status(401).body(Map.of("error","Invalid credentials"));
        String token = jwtTokenProvider.generateToken(u.getUsername(), 3600); // 1h
        return ResponseEntity.ok(Map.of("accessToken", token, "tokenType", "Bearer", "expiresIn", 3600));
    }

    /**
     * Notes about authentication endpoints:
     * - /auth/register and /auth/login are public to allow users to obtain credentials.
     * - Creating, listing and revoking API keys requires an authenticated principal.
     *   The controller accepts a Principal and will fallback to a supplied username
     *   in the request body only when necessary (useful for initial tests).
     */

    @PostMapping("/apikeys")
    public ResponseEntity<?> createApiKey(@RequestBody Map<String,String> body, java.security.Principal principal) {
        logger.info("Creando API key para el usuario: {}", principal != null ? principal.getName() : body.get("username"));
        String name = body.getOrDefault("name","default");
        String username = principal != null ? principal.getName() : body.get("username");
        if (username == null) return ResponseEntity.status(401).body(Map.of("error","No authenticated user"));
        User u = userService.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        String raw = apiKeyService.createForUser(u, name);
        // Return raw key once (user must copy it)
        return ResponseEntity.ok(Map.of("apiKey", raw));
    }

    @GetMapping("/apikeys")
    public ResponseEntity<?> listApiKeys(java.security.Principal principal) {
        logger.info("Listando API keys para el usuario: {}", principal != null ? principal.getName() : "N/A");
        if (principal == null) return ResponseEntity.status(401).body(Map.of("error","No authenticated user"));
        String username = principal.getName();
        User u = userService.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        var keys = apiKeyService.listForUser(u);
        // Don't return hashes
        var out = keys.stream().map(k -> Map.of("id", k.getId(), "name", k.getName(), "createdAt", k.getCreatedAt())).toList();
        return ResponseEntity.ok(out);
    }

    @DeleteMapping("/apikeys/{id}")
    public ResponseEntity<?> revokeApiKey(@PathVariable Long id, java.security.Principal principal) {
        logger.info("Eliminando API key para el usuario: {}", principal != null ? principal.getName() : "N/A");
        if (principal == null) return ResponseEntity.status(401).body(Map.of("error","No authenticated user"));
        String username = principal.getName();
        User u = userService.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        boolean removed = apiKeyService.revokeByIdForUser(id, u);
        if (removed) return ResponseEntity.ok(Map.of("status","revoked"));
        return ResponseEntity.status(404).body(Map.of("error","Not found"));
    }
}
