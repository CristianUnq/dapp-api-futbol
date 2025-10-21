package com.dapp.api_futbol.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.dapp.api_futbol.response.ResponseObject;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(TeamNotFoundException.class)
    public ResponseEntity<ResponseObject> handleTeamNotFound(TeamNotFoundException ex) {
        logger.warn("Equipo no encontrado: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ResponseObject(ex.getMessage(), HttpStatus.NOT_FOUND.value()));
    }

    @ExceptionHandler(InvalidTeamNameException.class)
    public ResponseEntity<ResponseObject> handleInvalidTeamName(InvalidTeamNameException ex) {
        logger.warn("Nombre de equipo inválido: {}", ex.getMessage());
        return ResponseEntity.badRequest()
            .body(new ResponseObject(ex.getMessage(), HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(ScrapingException.class)
    public ResponseEntity<ResponseObject> handleScrapingError(ScrapingException ex) {
        logger.error("Error al hacer scraping: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ResponseObject("Error al obtener datos del equipo", HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }

    @ExceptionHandler(ConnectionApiException.class)
    public ResponseEntity<ResponseObject> handleConnectionToApiError(ConnectionApiException ex) {
        logger.error("Error al conectar a la Api: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ResponseObject("Error al obtener datos de la Api", HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseObject> handleGenericException(Exception ex) {
        logger.error("Error inesperado: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ResponseObject("Ocurrió un error inesperado", HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }
}
