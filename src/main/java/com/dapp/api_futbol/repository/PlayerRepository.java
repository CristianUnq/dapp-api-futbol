package com.dapp.api_futbol.repository;

import com.dapp.api_futbol.model.Player;
import com.dapp.api_futbol.model.Team;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    Optional<Player> findByNameAndTeam(String name, Team team);

    // Devuelve todos los jugadores de un equipo
    java.util.List<Player> findByTeam(Team team);
}