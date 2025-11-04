package com.dapp.api_futbol.repository;

import com.dapp.api_futbol.model.PlayerStats;
import com.dapp.api_futbol.model.Player;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlayerStatsRepository extends JpaRepository<PlayerStats, Long> {
    
    @Query("SELECT ps FROM PlayerStats ps WHERE ps.player = :player ORDER BY ps.match.matchDate DESC")
    List<PlayerStats> findLastMatchesByPlayer(@Param("player") Player player, Pageable pageable);

    @Query("SELECT AVG(ps.rating) FROM PlayerStats ps WHERE ps.player = :player")
    Double getAverageRating(@Param("player") Player player);

    @Query("SELECT SUM(ps.goals) FROM PlayerStats ps WHERE ps.player = :player")
    Integer getTotalGoals(@Param("player") Player player);

    @Query("SELECT SUM(ps.assists) FROM PlayerStats ps WHERE ps.player = :player")
    Integer getTotalAssists(@Param("player") Player player);
}