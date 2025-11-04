package com.dapp.api_futbol.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class PlayerStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Player player;

    @ManyToOne
    private Match match;

    private Integer goals = 0;
    private Integer assists = 0;
    private Double rating = 0.0;
}