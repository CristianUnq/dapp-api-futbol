package com.dapp.api_futbol.repository;

import com.dapp.api_futbol.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player, Long> {
}