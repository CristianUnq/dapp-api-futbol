package com.dapp.api_futbol.controller;

import com.dapp.api_futbol.dto.QueryHistoryDTO;
import com.dapp.api_futbol.model.User;
import com.dapp.api_futbol.service.QueryHistoryService;
import com.dapp.api_futbol.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/history")
public class QueryHistoryController {

    private final QueryHistoryService queryHistoryService;
    private final UserService userService;

    public QueryHistoryController(QueryHistoryService queryHistoryService, UserService userService) {
        this.queryHistoryService = queryHistoryService;
        this.userService = userService;
    }

    @GetMapping("/queries")
    public ResponseEntity<?> getQueryHistory(Principal principal, Pageable pageable) {
        if (principal == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        User user = userService.findByUsername(principal.getName())
            .orElseThrow(() -> new RuntimeException("User not found"));

        Page<QueryHistoryDTO> history = queryHistoryService.getQueriesForUser(user, pageable);
        return ResponseEntity.ok(history);
    }
}