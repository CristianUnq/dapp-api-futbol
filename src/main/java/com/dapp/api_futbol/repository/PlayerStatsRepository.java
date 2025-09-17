package com.dapp.api_futbol.repository;

import com.dapp.api_futbol.model.PlayerStats;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerStatsRepository extends JpaRepository<PlayerStats, Long> {
}