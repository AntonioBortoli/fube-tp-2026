package com.tiendaonline.clientes.excepcion;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.View;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice

public class ManejadorGlobalExcepciones {

    private final View error;

    public ManejadorGlobalExcepciones(View error) {
        this.error = error;
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> manejarNoEncontrado(RecursoNoEncontradoException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .titulo("Recurso no encontrado")
                .estado(HttpStatus.NOT_FOUND.value())
                .detalle(ex.getMessage())
                .instancia(request.getRequestURI())
                .fecha(LocalDateTime.now())
                .build();


        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EmailDuplicadoException.class)
    public ResponseEntity<ErrorResponse> manejarEmailDuplicado(EmailDuplicadoException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .titulo("Email duplicado")
                .estado(HttpStatus.CONFLICT.value())
                .detalle(ex.getMessage())
                .instancia(request.getRequestURI())
                .fecha(LocalDateTime.now())
                .build();


        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> manejarValidacion(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String errores = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        ErrorResponse errorResponse = ErrorResponse.builder()
                .titulo("Validación fallida")
                .estado(HttpStatus.BAD_REQUEST.value())
                .detalle("Errores de validación: " + errores)
                .instancia(request.getRequestURI())
                .fecha(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> manejarException(Exception ex, HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .titulo("Error interno del servidor")
                .estado(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .detalle("Ocurrió un error inesperado al procesar la solicitud. ")
                .instancia(request.getRequestURI())
                .fecha(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}