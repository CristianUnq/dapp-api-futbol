package com.dapp.api_futbol.exception;

public class InvalidTeamNameException extends RuntimeException {
    public InvalidTeamNameException(String message) {
        super(message);
    }
}