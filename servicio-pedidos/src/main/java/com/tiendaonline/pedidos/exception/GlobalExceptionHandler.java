package com.tiendaonline.pedidos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;
/**
 * Manejador global de excepciones para la aplicación.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * Maneja la excepción PedidoNoEncontrado y devuelve una respuesta con el estado HTTP 404.
     * @param ex La excepción lanzada.
     * @return Una ResponseEntity con detalles del error.
     */
    @ExceptionHandler(PedidoNoEncontrado.class)
    public ResponseEntity<Map<String, Object>> handlePedidoNoEncontrado(PedidoNoEncontrado ex) {
        Map<String, Object> error = Map.of(
                "timestamp", LocalDateTime.now(),
                "status", HttpStatus.NOT_FOUND.value(),
                "error", "Not Found",
                "message", ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    /**
     * Maneja la excepción ClienteNoEncontrado y devuelve una respuesta con el estado HTTP 404.
     * @param ex La excepción lanzada.
     * @return Una ResponseEntity con detalles del error.
     */
    @ExceptionHandler(ClienteNoEncontrado.class)
    public ResponseEntity<Map<String, Object>> handleClienteNoEncontrado(ClienteNoEncontrado ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "timestamp", LocalDateTime.now(),
                "status", HttpStatus.NOT_FOUND.value(),
                "error", "Not Found",
                "message", ex.getMessage()
        ));
    }
    /**
     * Maneja la excepción StockInsuficiente y devuelve una respuesta con el estado HTTP 409.
     * @param ex La excepción lanzada.
     * @return Una ResponseEntity con detalles del error.
     */
    @ExceptionHandler(StockInsuficiente.class)
    public ResponseEntity<Map<String, Object>> handleStockInsuficiente(StockInsuficiente ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "timestamp", LocalDateTime.now(),
                "status", HttpStatus.CONFLICT.value(),
                "error", "Conflict",
                "message", ex.getMessage()
        ));
    }
}