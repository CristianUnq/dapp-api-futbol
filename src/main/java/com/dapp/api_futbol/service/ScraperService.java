package com.dapp.api_futbol.service;

import com.dapp.api_futbol.model.Player;
import com.dapp.api_futbol.model.Team;
import com.dapp.api_futbol.repository.PlayerRepository;
import com.dapp.api_futbol.repository.TeamRepository;
import com.dapp.api_futbol.response.ResponseObject;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ScraperService {

	private final TeamRepository teamRepository;
	private final PlayerRepository playerRepository;

	public ScraperService(TeamRepository teamRepository, PlayerRepository playerRepository) {
		this.teamRepository = teamRepository;
		this.playerRepository = playerRepository;
	}

	/**
	 * Devuelve los jugadores de un equipo buscando por nombre del equipo.
	 * Retorna un ResponseObject con data (lista de Player), mensaje y status.
	 */
	public ResponseObject getPlayersByTeam(String teamName) {
		if (teamName == null || teamName.trim().isEmpty()) {
			return new ResponseObject("Nombre de equipo inválido", HttpStatus.BAD_REQUEST.value());
		}

		// Try exact case-insensitive lookup first
		Optional<Team> teamOpt = teamRepository.findByNameIgnoreCase(teamName.trim());
		if (teamOpt.isEmpty()) {
			// fallback: try exact (legacy) or partial match
			teamOpt = teamRepository.findByName(teamName.trim());
		}

		if (teamOpt.isEmpty()) {
			return new ResponseObject("Equipo no encontrado: " + teamName, HttpStatus.NOT_FOUND.value());
		}

		Team team = teamOpt.get();
		List<Player> players = playerRepository.findByTeam(team);

		if (players == null || players.isEmpty()) {
			return new ResponseObject("No se encontraron jugadores para el equipo: " + team.getName(), HttpStatus.NOT_FOUND.value());
		}

		return new ResponseObject(players, "Jugadores encontrados exitosamente", HttpStatus.OK.value());
	}
}