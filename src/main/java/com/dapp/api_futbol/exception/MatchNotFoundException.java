package com.dapp.api_futbol.exception;

public class MatchNotFoundException extends RuntimeException {
    public MatchNotFoundException(String idMatch) {
        super("Partido no encontrado con ID: " + idMatch);
    }
}
