package com.dapp.api_futbol.service;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

import org.springframework.http.HttpStatus;
//import java.net.http.BodyHandlers;
import org.springframework.stereotype.Service;

import com.dapp.api_futbol.dto.MatchDTO;
import com.dapp.api_futbol.dto.TeamDTO;
import com.dapp.api_futbol.exception.ScrapingException;
import com.dapp.api_futbol.exception.TeamNotFoundException;
import com.dapp.api_futbol.exception.ConnectionApiException;
import com.dapp.api_futbol.model.Match;
import com.dapp.api_futbol.model.Team;
import com.dapp.api_futbol.model.TeamFootballData;
import com.dapp.api_futbol.response.ResponseObject;
import java.io.IOException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.text.Normalizer;

@Service
public class FootballDataService {
 
  //  @Value("${football.api.baseUrl}")
    private static final String BASE_URL = "https://api.football-data.org/v4/";

//    @Value("${football.api.token}")
    private String token = "a4759c66d2c74ec0bab2e83945e81a0d";

    private JsonNode connectionToApiFutbolData(String endpoint) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                                 .uri(URI.create(BASE_URL + endpoint))
                                 .header("X-Auth-Token", token)
                                 .build();

        try {
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            System.out.println(response.statusCode());
            System.out.println(response.body());

            ObjectMapper mapper = new ObjectMapper();
        
            // Parsear el JSON completo
            JsonNode rootNode = mapper.readTree(response.body());
            
            return rootNode;
        } catch (IOException | InterruptedException e) {
            throw new ConnectionApiException("Error al obtener informacion de la Api");
        }
        
    }

    private List<TeamFootballData> getTeamsFromResponse(JsonNode teamsCompetitionResponse) {
        // Obtener el array "teams"
        JsonNode teamsNode = teamsCompetitionResponse.get("teams");
        
        // Convertir el array de teams a una lista de objetos Team
        List<TeamFootballData> teams = new ArrayList<>();
        if (teamsNode.isArray()) {
            for (JsonNode teamNode : teamsNode) {
                TeamFootballData team = new TeamFootballData();
                team.setId(teamNode.get("id").asText());
                team.setName(teamNode.get("name").asText());
                teams.add(team);
            }
        }
    
        return teams;
    }

    public List<TeamFootballData> getAllTeams() {
        JsonNode teamsCompetitionResponse = connectionToApiFutbolData("competitions/" + "CL" + "/teams");
        return getTeamsFromResponse(teamsCompetitionResponse);
    }

    private String normalizeTeam(String s) {
        if (s == null) return "";
        String n = Normalizer.normalize(s, Normalizer.Form.NFD);
        n = n.replaceAll("\\p{M}+", "");
        n = n.toLowerCase().replaceAll("[^a-z0-9 ]", " ").replaceAll("\\s+", " ").trim();
        // remove common club tokens at start or end, with or without spaces (e.g., FCBarcelona, BarcelonaFC)
        n = n.replaceAll("^(fc|cf|club|sc|ac)\\s*", "");
        n = n.replaceAll("\\s*(fc|cf|club|sc|ac)$", "");
        n = n.replaceAll("\\s+", " ").trim();
        return n;
    }

    private String getIdTeamFromTeamsFootballData(String teamName, List<TeamFootballData> teamsFootballData) {
        return teamsFootballData.stream()
        .filter(team -> normalizeTeam(team.getName()).equals(normalizeTeam(teamName)))
        .findFirst()
        .map(TeamFootballData::getId)
        .orElseThrow(() -> new TeamNotFoundException("Equipo no encontrado: " + teamName));
    }

    private TeamDTO teamApiToModel(JsonNode teamNode) {
        TeamDTO team = new TeamDTO();
        team.setName(teamNode.get("name").asText());
        return team;
    }

    private List<MatchDTO> matchesApiToModel(JsonNode matchesNode) {
        List<MatchDTO> matches = new ArrayList<>();
        if (matchesNode.isArray()) {
            for (JsonNode matchNode : matchesNode) {
                MatchDTO match = new MatchDTO();
                match.setHomeTeam(teamApiToModel(matchNode.get("homeTeam")));
                match.setAwayTeam(teamApiToModel(matchNode.get("awayTeam")));
                match.setMatchDate(java.time.OffsetDateTime.parse(matchNode.get("utcDate").asText()).toLocalDateTime());
                match.setHomeScore(matchNode.get("score").get("fullTime").get("home").asInt());
                match.setAwayScore(matchNode.get("score").get("fullTime").get("away").asInt());
                
                matches.add(match);
            }
        }
        return matches;
    }

    public ResponseObject getNextMatchesOf(String teamName) {
        List<TeamFootballData> teamsFootballData = getAllTeams();
        String idTeam = getIdTeamFromTeamsFootballData(teamName, teamsFootballData);
        JsonNode teamMatchesresponse = connectionToApiFutbolData("teams/" + idTeam + "/matches?competitions=CL&status=SCHEDULED");
        List<MatchDTO> nextMatches = matchesApiToModel(teamMatchesresponse.get("matches"));
        ResponseObject responseNextMatches = new ResponseObject(nextMatches, "Proximos partidos encontrados exitosamente", HttpStatus.OK.value());
        return responseNextMatches;
    }
}