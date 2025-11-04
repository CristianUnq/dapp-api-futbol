package com.dapp.api_futbol.repository;

import com.dapp.api_futbol.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MatchRepository extends JpaRepository<Match, Long> {
    Optional<Match> findByFootballDataId(Integer footballDataId);

    @Query("SELECT m FROM Match m WHERE m.status = :status AND (LOWER(m.homeTeamName) LIKE CONCAT('%', :name, '%') OR LOWER(m.awayTeamName) LIKE CONCAT('%', :name, '%')) ORDER BY m.matchDate ASC")
    List<Match> findUpcomingByTeamNameAndStatus(@Param("name") String name, @Param("status") String status);
}