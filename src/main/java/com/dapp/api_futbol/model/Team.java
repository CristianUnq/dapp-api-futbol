package com.dapp.api_futbol.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "team")
    private List<Player> players;

    public void setName(String string) {
        name = string;
    }

    public String getId() {
        return id.toString();
    }

    public String getName() {
        return name;
    }
}