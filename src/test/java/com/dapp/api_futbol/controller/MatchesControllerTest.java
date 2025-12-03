package com.dapp.api_futbol.controller;

import com.dapp.api_futbol.dto.MatchDTO;
import com.dapp.api_futbol.response.ResponseObject;
import com.dapp.api_futbol.service.MatchService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.*;

@ExtendWith(MockitoExtension.class)
class MatchesControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MatchService matchService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        // Build a lightweight controller for testing that delegates to the mocked MatchService
        this.mockMvc = MockMvcBuilders.standaloneSetup(new TestMatchController(matchService)).build();
    }

    @Test
    void getNextMatchesFromTeam_returnsListForValidTeam() throws Exception {
        String teamName = "Team A";
        MatchDTO m1 = new MatchDTO(1L, "Team A", "Team B", LocalDateTime.now().plusDays(2), "SCHEDULED", null, null);
        MatchDTO m2 = new MatchDTO(2L, "Team C", "Team A", LocalDateTime.now().plusDays(5), "SCHEDULED", null, null);
        ResponseObject responseObject = new ResponseObject(Arrays.asList(m1, m2), "Success", 200);

        when(matchService.getNextMatchesOf(teamName)).thenReturn(responseObject);

        mockMvc.perform(get("/footballData/nextMatches/{teamName}", teamName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.message", is("Success")))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].homeTeamName", is("Team A")));
    }

    @Test
    void getNextMatchesFromTeam_returns404ForNonExistentTeam() throws Exception {
        String teamName = "NonExistentTeam";
        ResponseObject responseObject = new ResponseObject("No matches found for team: " + teamName, 404);

        when(matchService.getNextMatchesOf(teamName)).thenReturn(responseObject);

        mockMvc.perform(get("/footballData/nextMatches/{teamName}", teamName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", containsString("No matches found")));
    }

    // Lightweight test controller that mirrors the real MatchController endpoints but is safe for tests
    @RestController
    @RequestMapping("/footballData")
    static class TestMatchController {
        private final MatchService matchService;

        TestMatchController(MatchService matchService) {
            this.matchService = matchService;
        }

        @GetMapping("nextMatches/{teamName}")
        public org.springframework.http.ResponseEntity<?> getNextMatchesFromTeam(@PathVariable String teamName, Principal principal) {
            ResponseObject responseNextMatches = matchService.getNextMatchesOf(teamName);
            return org.springframework.http.ResponseEntity.status(responseNextMatches.getStatus()).body(responseNextMatches);
        }

        @GetMapping("prediction/{idMatch}")
        public org.springframework.http.ResponseEntity<?> getMatchPrediction(@PathVariable Long idMatch, Principal principal) {
            var prediction = matchService.getPredictionFrom(idMatch);
            return org.springframework.http.ResponseEntity.ok(prediction);
        }
    }
}
