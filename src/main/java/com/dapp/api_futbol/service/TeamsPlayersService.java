package com.dapp.api_futbol.service;
import com.dapp.api_futbol.dto.PlayerDTO;
import com.dapp.api_futbol.exception.InvalidTeamNameException;
import com.dapp.api_futbol.exception.ScrapingException;
import com.dapp.api_futbol.exception.TeamNotFoundException;
import com.dapp.api_futbol.response.ResponseObject;

import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;

import java.util.List;

@Service
public class TeamsPlayersService {

    private final ScraperService scraperService;

    public TeamsPlayersService(ScraperService scraperService) {
        this.scraperService = scraperService;
    }

    public ResponseObject getPlayersByTeam(String teamName) {
        // Validación
        if (teamName == null || teamName.trim().isEmpty()) {
            throw new InvalidTeamNameException("El nombre del equipo no puede estar vacío");
        }

        String normalizedTeamName = teamName.trim();
        try {
            List<PlayerDTO> players = scraperService.scrapePlayersByTeam(normalizedTeamName);
            
            if (players == null || players.isEmpty()) {
                throw new TeamNotFoundException(normalizedTeamName);
            }
            
            ResponseObject responsePlayers = new ResponseObject(players, "Jugadores encontrados exitosamente", HttpStatus.OK.value());
            return responsePlayers;
            
        } catch (TeamNotFoundException | InvalidTeamNameException e) {
            throw e; // Re-lanzar excepciones de negocio
        } catch (Exception e) {
            throw new ScrapingException("Error al hacer scraping del equipo: " + normalizedTeamName, e);
        }
    }

}