package com.dapp.api_futbol.service;

import com.dapp.api_futbol.dto.QueryHistoryDTO;
import com.dapp.api_futbol.model.QueryHistory;
import com.dapp.api_futbol.model.User;
import com.dapp.api_futbol.repository.QueryHistoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class QueryHistoryService {

    private final QueryHistoryRepository queryHistoryRepository;

    public QueryHistoryService(QueryHistoryRepository queryHistoryRepository) {
        this.queryHistoryRepository = queryHistoryRepository;
    }

    @Transactional
    public void recordQuery(User user, String queryType, String queryParams) {
        QueryHistory history = new QueryHistory(user, queryType, queryParams);
        queryHistoryRepository.save(history);
    }

    @Transactional(readOnly = true)
    public Page<QueryHistoryDTO> getQueriesForUser(User user, Pageable pageable) {
        return queryHistoryRepository.findByUserIdOrderByTimestampDesc(user.getId(), pageable)
            .map(h -> new QueryHistoryDTO(h.getId(), h.getQueryType(), h.getQueryParams(), h.getTimestamp()));
    }
}