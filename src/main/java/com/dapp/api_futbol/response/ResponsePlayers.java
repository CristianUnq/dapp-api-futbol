package com.dapp.api_futbol.response;
import java.time.LocalDateTime;

public class ResponsePlayers {
    private Object data;
    private String message;
    private int status;
    private LocalDateTime timestamp;

    public ResponsePlayers(Object data, String message, int status) {
        this.data = data;
        this.message = message;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }
    
    public ResponsePlayers(String message, int status) {
        this.message = message;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }

    // Getters
    public Object getData() { return data; }
    public String getMessage() { return message; }
    public int getStatus() { return status; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
