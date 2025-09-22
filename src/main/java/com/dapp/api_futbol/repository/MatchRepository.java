package com.dapp.api_futbol.repository;

import com.dapp.api_futbol.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchRepository extends JpaRepository<Match, Long> {
}