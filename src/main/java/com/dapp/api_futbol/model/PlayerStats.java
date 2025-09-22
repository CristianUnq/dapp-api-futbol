package com.dapp.api_futbol.model;

import jakarta.persistence.*;

@Entity
public class PlayerStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Player player;

    @ManyToOne
    private Match match;

    private Integer goals;
    private Integer assists;
    private Double rating;

    // Add more fields as needed

    // Getters and setters
    // ...
}