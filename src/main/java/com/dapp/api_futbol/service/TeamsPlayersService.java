package com.dapp.api_futbol.service;

import com.dapp.api_futbol.model.Player;
import com.dapp.api_futbol.model.Team;
import com.dapp.api_futbol.repository.PlayerRepository;
import com.dapp.api_futbol.repository.TeamRepository;
import com.dapp.api_futbol.response.ResponseObject;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TeamsPlayersService {

	private final TeamRepository teamRepository;
	private final PlayerRepository playerRepository;

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(TeamsPlayersService.class);

	public TeamsPlayersService(TeamRepository teamRepository, PlayerRepository playerRepository) {
		this.teamRepository = teamRepository;
		this.playerRepository = playerRepository;
	}

	/**
	 * Devuelve los jugadores de un equipo buscando por nombre del equipo.
	 * Retorna un ResponseObject con data (lista de Player), mensaje y status.
	 */
	public ResponseObject getPlayersByTeam(String teamName) {
		if (teamName == null || teamName.trim().isEmpty()) {
			logger.error("Es Invalido el nombre del equipo: {}", teamName);
			return new ResponseObject("Nombre de equipo inválido", HttpStatus.BAD_REQUEST.value());
		}

		// Try exact case-insensitive lookup first
		logger.info("buscando equipo con nombre: {}", teamName);
		Optional<Team> teamOpt = teamRepository.findByNameIgnoreCase(teamName.trim());
		if (teamOpt.isEmpty()) {
			// Fallback to contains lookup
			teamOpt = teamRepository.findByName(teamName.trim());
		}

		if (teamOpt.isEmpty()) {
			logger.error("No se encontro el equipo con nombre: {}", teamName);
			return new ResponseObject("Equipo no encontrado: " + teamName, HttpStatus.NOT_FOUND.value());
		}

		logger.info("Se encontro el equipo con nombre: {}", teamName);
		Team team = teamOpt.get();
		logger.info("Buscando jugadores para el equipo: {}", teamName);
		List<Player> players = playerRepository.findByTeam(team);

		if (players == null || players.isEmpty()) {
			logger.error("No se encontraron jugadores para el equipo: {}", teamName);
			return new ResponseObject("No se encontraron jugadores para el equipo: " + team.getName(), HttpStatus.NOT_FOUND.value());
		}

		logger.error("Se encontraron exitosamente los jugadores para el equipo: {}", teamName);
		return new ResponseObject(players, "Jugadores encontrados exitosamente", HttpStatus.OK.value());
	}
}