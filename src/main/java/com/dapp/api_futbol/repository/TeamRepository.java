package com.dapp.api_futbol.repository;

import com.dapp.api_futbol.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
}