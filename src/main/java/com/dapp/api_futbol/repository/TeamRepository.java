package com.dapp.api_futbol.repository;

import com.dapp.api_futbol.model.Match;
import com.dapp.api_futbol.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {
    Optional<Team> findByName(String name);

    @Query("SELECT m FROM Team m WHERE :name LIKE CONCAT('%', m.name, '%')")
    Optional<Team> findByNameOrContainsName(@Param("name") String name);

    // case-insensitive lookup convenience
    Optional<Team> findByNameIgnoreCase(String name);
}