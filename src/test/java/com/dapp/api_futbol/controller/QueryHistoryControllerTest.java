package com.dapp.api_futbol.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.dapp.api_futbol.dto.QueryHistoryDTO;
import com.dapp.api_futbol.model.User;
import com.dapp.api_futbol.service.QueryHistoryService;
import com.dapp.api_futbol.service.UserService;

@WebMvcTest(QueryHistoryController.class)
class QueryHistoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QueryHistoryService queryHistoryService;

    @MockBean
    private UserService userService;

    @Test
    @WithMockUser(username = "testuser")
    void getQueryHistory_ReturnsPagedHistory() throws Exception {
        // given
        User user = new User("testuser", "pass");
        QueryHistoryDTO query1 = new QueryHistoryDTO(1L, "GET_PLAYERS", "team=Barcelona", LocalDateTime.now());
        QueryHistoryDTO query2 = new QueryHistoryDTO(2L, "GET_NEXT_MATCHES", "team=RealMadrid", LocalDateTime.now());
        Page<QueryHistoryDTO> page = new PageImpl<>(Arrays.asList(query1, query2));

        when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(queryHistoryService.getQueriesForUser(eq(user), any(PageRequest.class))).thenReturn(page);

        // when/then
        mockMvc.perform(get("/api/history/queries"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].queryType").value("GET_PLAYERS"))
            .andExpect(jsonPath("$.content[0].queryParams").value("team=Barcelona"))
            .andExpect(jsonPath("$.content[1].queryType").value("GET_NEXT_MATCHES"))
            .andExpect(jsonPath("$.content[1].queryParams").value("team=RealMadrid"));
    }

    @Test
    void getQueryHistory_WithoutAuth_Returns401() throws Exception {
        mockMvc.perform(get("/api/history/queries"))
            .andExpect(status().isUnauthorized());
    }
}