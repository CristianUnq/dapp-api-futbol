package com.dapp.api_futbol.repository;

import com.dapp.api_futbol.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

public interface MatchRepository extends JpaRepository<Match, Long> {
    Optional<Match> findByFootballDataId(Integer footballDataId);

    @Query("SELECT m FROM Match m WHERE m.status = :status AND (LOWER(m.homeTeamName) LIKE CONCAT('%', :name, '%') OR LOWER(m.awayTeamName) LIKE CONCAT('%', :name, '%')) ORDER BY m.matchDate ASC")
    List<Match> findUpcomingByTeamNameAndStatus(@Param("name") String name, @Param("status") String status);

    @Query("SELECT m FROM Match m WHERE m.matchDate > :now AND (LOWER(m.homeTeamName) LIKE CONCAT('%', :name, '%') OR LOWER(m.awayTeamName) LIKE CONCAT('%', :name, '%')) ORDER BY m.matchDate ASC")
    List<Match> findUpcomingByTeamName(@Param("name") String name, @Param("now") LocalDateTime now);

    @Query("SELECT m FROM Match m WHERE (LOWER(m.homeTeamName) = LOWER(:a) AND LOWER(m.awayTeamName) = LOWER(:b)) OR (LOWER(m.homeTeamName) = LOWER(:b) AND LOWER(m.awayTeamName) = LOWER(:a)) ORDER BY m.matchDate DESC")
    List<Match> findHeadToHead(@Param("a") String a, @Param("b") String b);

    @Query("SELECT m FROM Match m WHERE LOWER(m.homeTeamName) = LOWER(:name) OR LOWER(m.awayTeamName) = LOWER(:name)")
    List<Match> findMatchesByTeamName(@Param("name") String name);
}