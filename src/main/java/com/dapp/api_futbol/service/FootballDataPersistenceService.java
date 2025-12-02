package com.dapp.api_futbol.service;

import com.dapp.api_futbol.model.Match;
import com.dapp.api_futbol.repository.MatchRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Optional;

@Service
public class FootballDataPersistenceService {

    @Value("${football.data.base_url}")
    private String BASE_URL;
    @Value("${football.data.token}")
    private String token;

    private final MatchRepository matchRepository;

    public FootballDataPersistenceService(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    @Scheduled(initialDelay = 0, fixedRate = 24 * 60 * 60 * 1000)
    @Transactional
    public void syncChampionsLeagueMatches() {
        System.out.println("Iniciando la sincronización de partidos de la Champions League...");
        JsonNode matchesNode = connectionToApiFutbolData("competitions/CL/matches");
        if (matchesNode != null && matchesNode.has("matches")) {
            saveMatches(matchesNode.get("matches"));
        }
        System.out.println("Sincronización de partidos completada.");
    }

    private void saveMatches(JsonNode matchesArray) {
        if (!matchesArray.isArray()) return;

        for (JsonNode matchNode : matchesArray) {
            Integer matchId = matchNode.get("id").asInt();

            Optional<Match> existingMatch = matchRepository.findByFootballDataId(matchId);
            if (existingMatch.isPresent()) continue;

            String homeTeamName = matchNode.get("homeTeam").get("name").asText();
            String awayTeamName = matchNode.get("awayTeam").get("name").asText();

            Match match = new Match();
            match.setFootballDataId(matchId);
            match.setHomeTeamName(homeTeamName);
            match.setAwayTeamName(awayTeamName);

            LocalDateTime matchDate = OffsetDateTime.parse(matchNode.get("utcDate").asText()).toLocalDateTime();
            match.setMatchDate(matchDate);

            match.setStatus(matchNode.get("status").asText());

            if (matchNode.get("score").get("fullTime").has("home") && !matchNode.get("score").get("fullTime").get("home").isNull()) {
                match.setHomeScore(matchNode.get("score").get("fullTime").get("home").asInt());
            }
            if (matchNode.get("score").get("fullTime").has("away") && !matchNode.get("score").get("fullTime").get("away").isNull()) {
                match.setAwayScore(matchNode.get("score").get("fullTime").get("away").asInt());
            }

            matchRepository.save(match);
        }
    }

    private JsonNode connectionToApiFutbolData(String endpoint) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("X-Auth-Token", token)
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                System.err.println("Error en la API de FootballData: " + response.statusCode() + " " + response.body());
                return null;
            }
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readTree(response.body());
        } catch (IOException | InterruptedException e) {
            System.err.println("Error al conectar con la API de FootballData: " + e.getMessage());
            return null;
        }
    }
}
