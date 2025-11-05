package com.dapp.api_futbol.metrics;

import com.dapp.api_futbol.model.Player;
import com.dapp.api_futbol.model.Team;
import com.dapp.api_futbol.repository.PlayerRepository;
import com.dapp.api_futbol.repository.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PlayerPerformanceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private TeamRepository teamRepository;

    private Long playerId;

    @BeforeEach
    void setup() {
        playerRepository.deleteAll();
        teamRepository.deleteAll();

        Team team = new Team();
        team.setName("Inter Miami");
        team = teamRepository.save(team);

        Player p = new Player();
        p.setName("Lionel Messi");
        p.setTeam(team);
        p.setAge(36);
        p.setPosition("Forward");
        p.setGoals("15");
        p.setAssists("10");
        p.setRating("8.5");

        p = playerRepository.save(p);
        playerId = p.getId();
    }

    @Test
    void shouldReturnPlayerPerformanceFromDb() throws Exception {
        mockMvc.perform(get("/players/performance/" + playerId)
                        .with(user("testuser"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.playerId").value(playerId))
                .andExpect(jsonPath("$.playerName").value("Lionel Messi"))
                .andExpect(jsonPath("$.teamName").value("Inter Miami"))
                .andExpect(jsonPath("$.averageRating").value(8.5))
                .andExpect(jsonPath("$.totalGoals").value(15))
                .andExpect(jsonPath("$.totalAssists").value(10))

                // métricas calculadas
                .andExpect(jsonPath("$.matchesPlayed").value(20))
                .andExpect(jsonPath("$.goalsPerMatch").value(0.75))
                .andExpect(jsonPath("$.assistsPerMatch").value(0.50))
                .andExpect(jsonPath("$.goalContributionsPerMatch").value(1.25))
                .andExpect(jsonPath("$.normalizedGoalContrib").value(0.833))
                .andExpect(jsonPath("$.performanceIndex").value(8.433))
                .andExpect(jsonPath("$.attackImpact").value(4.5))
                .andExpect(jsonPath("$.tierRating").value("A"));
    }
}