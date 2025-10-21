package com.dapp.api_futbol.model;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * Entity that represents an API key associated with a user.
 *
 * Implementation notes:
 * - The raw API key is only returned once at creation and never stored in plaintext.
 * - `keyHash` stores a bcrypt hash of the raw key for secure verification.
 * - `keyFingerprint` stores a fixed-length, indexable fingerprint (SHA-256 Base64URL)
 *   to allow efficient lookup without scanning the whole table. After finding by
 *   fingerprint we still verify the bcrypt hash to avoid accidental collisions.
 */
@Entity
@Table(name = "api_keys", indexes = {@Index(name = "idx_key_fingerprint", columnList = "key_fingerprint")})
public class ApiKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Friendly name given by the user to identify the key (e.g. "cli-key"). */
    @Column(nullable = false)
    private String name;

    /** Bcrypt hash of the raw API key. Used for secure verification. */
    @Column(nullable = false)
    private String keyHash;

    /**
     * Fixed-length fingerprint (SHA-256, Base64URL) stored to allow indexed lookup.
     * We do not rely solely on this for security; bcrypt is used to verify the raw key.
     */
    @Column(name = "key_fingerprint", nullable = false)
    private String keyFingerprint;

    /** Creation timestamp. */
    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    /** Owning user (many keys can belong to one user). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public ApiKey() {}

    public ApiKey(String name, String keyHash, String keyFingerprint, User user) {
        this.name = name;
        this.keyHash = keyHash;
        this.keyFingerprint = keyFingerprint;
        this.user = user;
    }

    // Getters / setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getKeyHash() { return keyHash; }
    public void setKeyHash(String keyHash) { this.keyHash = keyHash; }
    public String getKeyFingerprint() { return keyFingerprint; }
    public void setKeyFingerprint(String keyFingerprint) { this.keyFingerprint = keyFingerprint; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}

