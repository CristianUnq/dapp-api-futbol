package com.dapp.api_futbol.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "query_history")
public class QueryHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User user;

    private String queryType;
    private String queryParams;
    private LocalDateTime timestamp;

    protected QueryHistory() {}

    public QueryHistory(User user, String queryType, String queryParams) {
        this.user = user;
        this.queryType = queryType;
        this.queryParams = queryParams;
        this.timestamp = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
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