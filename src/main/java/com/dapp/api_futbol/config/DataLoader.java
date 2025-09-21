package com.dapp.api_futbol.config;

import com.dapp.api_futbol.dto.PlayerDTO;
import com.dapp.api_futbol.model.Player;
import com.dapp.api_futbol.model.Team;
import com.dapp.api_futbol.repository.MatchRepository; // Importar MatchRepository
import com.dapp.api_futbol.repository.PlayerRepository;
import com.dapp.api_futbol.repository.TeamRepository;
import com.dapp.api_futbol.service.ScraperService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final ScraperService scraperService;
    private final MatchRepository matchRepository; // Añadir MatchRepository

    public DataLoader(TeamRepository teamRepository, PlayerRepository playerRepository, ScraperService scraperService, MatchRepository matchRepository) { // Actualizar constructor
        this.teamRepository = teamRepository;
        this.playerRepository = playerRepository;
        this.scraperService = scraperService;
        this.matchRepository = matchRepository; // Asignar
    }

    @Override
    public void run(String... args) throws Exception {
        // Corregir el orden de borrado para evitar errores de foreign key
        matchRepository.deleteAll();
        playerRepository.deleteAll();
        teamRepository.deleteAll();

        // 1. Crear y guardar el equipo
        Team liverpool = new Team();
        liverpool.setName("Liverpool");
        teamRepository.save(liverpool);
        System.out.println("Equipo 'Liverpool' guardado en la base de datos.");

        // 2. Usar el scraper para obtener los jugadores
        System.out.println("Obteniendo jugadores de Liverpool con el scraper...");
        List<PlayerDTO> playerDTOs = scraperService.scrapePlayers("Liverpool");

        // 3. Convertir DTOs a entidades y guardarlos
        for (PlayerDTO dto : playerDTOs) {
            Player player = new Player();
            player.setName(dto.getName());
            player.setTeam(liverpool); // Asignar el equipo que creamos
            playerRepository.save(player);
        }

        System.out.println(playerDTOs.size() + " jugadores de Liverpool guardados en la base de datos.");
    }
}