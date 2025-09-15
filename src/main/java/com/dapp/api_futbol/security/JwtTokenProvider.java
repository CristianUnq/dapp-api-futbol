package com.dapp.api_futbol.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.nio.charset.StandardCharsets;

@Component
public class JwtTokenProvider {

    private final Key key;

    public JwtTokenProvider(@Value("${app.jwt.secret:secretkeyshouldbereplaced}") String secret) {
        byte[] keyBytes;
        // Try decode as Base64 first (common when storing binary keys)
        try {
            keyBytes = java.util.Base64.getDecoder().decode(secret);
        } catch (IllegalArgumentException ex) {
            // Not valid Base64, fall back to raw bytes (UTF-8)
            keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        }

        // Ensure key is strong enough for HMAC-SHA (>= 256 bits -> 32 bytes)
        if (keyBytes.length < 32) {
            throw new IllegalArgumentException("JWT secret is too short (" + keyBytes.length + " bytes). " +
                    "Use a 32+ byte secret or provide a Base64-encoded 32-byte key. Example to generate one:\n" +
                    "PowerShell: $b=New-Object byte[] 32; (New-Object System.Security.Cryptography.RNGCryptoServiceProvider).GetBytes($b); [Convert]::ToBase64String($b) | Set-Clipboard");
        }

        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public Jws<Claims> validateToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    }

}
