package com.dapp.api_futbol.controller;

import com.dapp.api_futbol.dto.MatchDTO;
import com.dapp.api_futbol.service.MatchService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.*;

@WebMvcTest(MatchesController.class)
@Import(com.dapp.api_futbol.config.SecurityConfig.class)
class MatchesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MatchService matchService;

    @MockBean
    private com.dapp.api_futbol.security.JwtTokenProvider jwtTokenProvider;

    @MockBean
    private com.dapp.api_futbol.service.ApiKeyService apiKeyService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getUpcomingMatches_returnsList() throws Exception {
        MatchDTO m1 = new MatchDTO(1L, "Team A", "Team B", LocalDateTime.now().plusDays(2), "SCHEDULED", null, null);
        MatchDTO m2 = new MatchDTO(2L, "Team C", "Team A", LocalDateTime.now().plusDays(5), "SCHEDULED", null, null);

        when(matchService.getUpcomingMatchesByTeam("Team A")).thenReturn(Arrays.asList(m1, m2));

    mockMvc.perform(get("/matches/upcoming").param("team", "Team A").with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("testuser")).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].homeTeamName", is("Team A")));
    }
}
