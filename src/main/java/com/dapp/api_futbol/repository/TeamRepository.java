package com.dapp.api_futbol.repository;

import com.dapp.api_futbol.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {
    Optional<Team> findByName(String name);

    // case-insensitive lookup convenience
    Optional<Team> findByNameIgnoreCase(String name);
}