package com.dapp.api_futbol.controller;

import com.dapp.api_futbol.config.TestSecurityConfig;
import com.dapp.api_futbol.dto.PlayerPerformanceDTO;
import com.dapp.api_futbol.service.PlayerPerformanceService;
import org.springframework.context.annotation.Import;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PlayerPerformanceController.class)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
public class PlayerPerformanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlayerPerformanceService playerPerformanceService;

    @Test
    @WithMockUser
    void getPlayerPerformance_ShouldReturnPlayerStats() throws Exception {
        // Arrange
        PlayerPerformanceDTO mockDto = new PlayerPerformanceDTO();
        mockDto.setPlayerId(1L);
        mockDto.setPlayerName("Lionel Messi");
        mockDto.setTeamName("Inter Miami");
        mockDto.setAverageRating(8.5);
        mockDto.setTotalGoals(15);
        mockDto.setTotalAssists(10);
        mockDto.setLastMatches(new ArrayList<>());

        when(playerPerformanceService.getPlayerPerformance(anyLong())).thenReturn(mockDto);

        // Act & Assert
        mockMvc.perform(get("/players/performance/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.playerId").value(1))
                .andExpect(jsonPath("$.playerName").value("Lionel Messi"))
                .andExpect(jsonPath("$.teamName").value("Inter Miami"))
                .andExpect(jsonPath("$.averageRating").value(8.5))
                .andExpect(jsonPath("$.totalGoals").value(15))
                .andExpect(jsonPath("$.totalAssists").value(10));
    }

    @Test
    void getPlayerPerformance_WithoutAuth_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/players/performance/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}