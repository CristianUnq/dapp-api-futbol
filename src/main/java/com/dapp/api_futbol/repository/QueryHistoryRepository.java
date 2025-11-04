package com.dapp.api_futbol.repository;

import com.dapp.api_futbol.model.QueryHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QueryHistoryRepository extends JpaRepository<QueryHistory, Long> {
    Page<QueryHistory> findByUserIdOrderByTimestampDesc(Long userId, Pageable pageable);
}