package com.dapp.api_futbol.exception;

public class TeamNotFoundException extends RuntimeException {
    public TeamNotFoundException(String teamName) {
        super("No se encontraron jugadores para el equipo: " + teamName);
    }
}
