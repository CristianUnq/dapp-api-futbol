package com.dapp.api_futbol.controller;

import com.dapp.api_futbol.dto.PlayerDTO;
import com.dapp.api_futbol.response.ResponseObject;
import com.dapp.api_futbol.service.TeamsPlayersService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TeamsPlayersController.class)
public class TeamsPlayersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TeamsPlayersService teamsPlayersService;

    @Test
    @DisplayName("GET /api/players/{teamName} returns player list for team")
    @WithMockUser
    void getPlayersOfTeam_returnsPlayerList() throws Exception {
        // Arrange: prepare a couple of PlayerDTOs
        PlayerDTO p1 = new PlayerDTO();
        p1.setName("Lionel Messi");
        p1.setTeam("Inter Miami");
        p1.setGoals("5");

        PlayerDTO p2 = new PlayerDTO();
        p2.setName("Christian Bentancur");
        p2.setTeam("Inter Miami");
        p2.setGoals("1");

        var players = List.of(p1, p2);
        ResponseObject resp = new ResponseObject(players, "Jugadores encontrados exitosamente", 200);

        when(teamsPlayersService.getPlayersByTeam("Inter Miami")).thenReturn(resp);

        // Act & Assert: call endpoint and verify JSON contains the players
        mockMvc.perform(get("/api/players/{teamName}", "Inter Miami").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                // data is an array, check first element name and team
                .andExpect(jsonPath("$.data[0].name").value("Lionel Messi"))
                .andExpect(jsonPath("$.data[0].team").value("Inter Miami"))
                .andExpect(jsonPath("$.data[1].name").value("Christian Bentancur"));
    }
}
