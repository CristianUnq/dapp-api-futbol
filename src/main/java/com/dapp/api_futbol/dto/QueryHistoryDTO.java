package com.dapp.api_futbol.dto;

import java.time.LocalDateTime;

public class QueryHistoryDTO {
    private Long id;
    private String queryType;
    private String queryParams;
    private LocalDateTime timestamp;

    public QueryHistoryDTO(Long id, String queryType, String queryParams, LocalDateTime timestamp) {
        this.id = id;
        this.queryType = queryType;
        this.queryParams = queryParams;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public String getQueryType() {
        return queryType;
    }

    public String getQueryParams() {
        return queryParams;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}