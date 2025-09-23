package com.dapp.api_futbol.config;

import com.dapp.api_futbol.dto.PlayerDTO;
import com.dapp.api_futbol.model.Player;
import com.dapp.api_futbol.model.Team;
import com.dapp.api_futbol.repository.MatchRepository;
import com.dapp.api_futbol.repository.PlayerRepository;
import com.dapp.api_futbol.repository.TeamRepository;
import com.dapp.api_futbol.service.ScraperService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Loads initial data only when explicitly enabled via property
 * app.data-loader.enabled=true. By default the bean won't be created,
 * which prevents side-effects during tests and CI.
 */
@ConditionalOnProperty(name = "app.data-loader.enabled", havingValue = "true", matchIfMissing = false)
public class DataLoader implements CommandLineRunner {

    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final ScraperService scraperService;
    private final MatchRepository matchRepository;

    public DataLoader(TeamRepository teamRepository, PlayerRepository playerRepository, ScraperService scraperService, MatchRepository matchRepository) {
        this.teamRepository = teamRepository;
        this.playerRepository = playerRepository;
        this.scraperService = scraperService;
        this.matchRepository = matchRepository;
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
        List<PlayerDTO> playerDTOs = scraperService.scrapePlayersByTeam("Liverpool");

        // 3. Imprimir los jugadores obtenidos en la consola
        System.out.println("\n--- Jugadores de Liverpool ---");
        for (PlayerDTO dto : playerDTOs) {
            System.out.println(dto.toString());
        }
        System.out.println("--- Fin de la lista ---\n");

        // 4. Convertir DTOs a entidades y guardarlos
        for (PlayerDTO dto : playerDTOs) {
            Player player = new Player();
            player.setName(dto.getName());
            player.setTeam(liverpool); // Asignar el equipo que creamos

            // Asignar el resto de los atributos
            player.setHeight(dto.getHeight());
            player.setWeight(dto.getWeight());
            player.setAppearances(dto.getAppearances());
            player.setGoals(dto.getGoals());
            player.setAssists(dto.getAssists());
            player.setRating(dto.getRating());
            player.setUrl(dto.getUrl());
            player.setMinsPlayed(dto.getMinsPlayed());
            player.setYellowCards(dto.getYellowCards());
            player.setRedCards(dto.getRedCards());
            player.setShotsPerGame(dto.getShotsPerGame());
            player.setPassSuccess(dto.getPassSuccess());
            player.setAerialsWon(dto.getAerialsWon());
            player.setManOfTheMatch(dto.getManOfTheMatch());

            playerRepository.save(player);
        }
        System.out.println(playerDTOs.size() + " jugadores de Liverpool guardados en la base de datos.");
    }
}