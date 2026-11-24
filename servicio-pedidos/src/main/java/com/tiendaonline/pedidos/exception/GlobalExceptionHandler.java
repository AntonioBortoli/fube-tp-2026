package com.tiendaonline.pedidos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;
/**
 * Manejador global de excepciones para la aplicación de pedidos.
 * Captura excepciones específicas y genera respuestas HTTP adecuadas con detalles del error.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * Construye una respuesta de error estándar para las excepciones manejadas.
     * @param status El estado HTTP que se desea retornar.
     * @param message El mensaje de error que se desea incluir en la respuesta.
     * @return ResponseEntity con un mapa que contiene detalles del error.
     */
    private ResponseEntity<Map<String, Object>> buildError(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(Map.of(
                "timestamp", LocalDateTime.now(),
                "status", status.value(),
                "error", status.getReasonPhrase(),
                "message", message
        ));
    }
    /**
     * Maneja las excepciones de pedido no encontrado.
     * @param ex La excepción lanzada cuando un pedido no es encontrado.
     * @return ResponseEntity con el estado HTTP 404 y un mensaje detallado del error.
     */
    @ExceptionHandler(PedidoNoEncontrado.class)
    public ResponseEntity<Map<String, Object>> handlePedidoNoEncontrado(PedidoNoEncontrado ex) {
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage());
    }
    /**
     * Maneja las excepciones de cliente no encontrado.
     * @param ex La excepción lanzada cuando un cliente no es encontrado.
     * @return ResponseEntity con el estado HTTP 404 y un mensaje detallado del error.
     */
    @ExceptionHandler(ClienteNoEncontrado.class)
    public ResponseEntity<Map<String, Object>> handleClienteNoEncontrado(ClienteNoEncontrado ex) {
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage());
    }
    /**
     * Maneja las excepciones de stock insuficiente.
     * @param ex La excepción lanzada cuando no hay suficiente stock para un producto.
     * @return ResponseEntity con el estado HTTP 409 y un mensaje detallado del error.
     */
    @ExceptionHandler(StockInsuficiente.class)
    public ResponseEntity<Map<String, Object>> handleStockInsuficiente(StockInsuficiente ex) {
        return buildError(HttpStatus.CONFLICT, ex.getMessage());
    }
    /**
     * Maneja las excepciones de estado de pedido inválido.
     * @param ex La excepción lanzada cuando se intenta realizar una operación sobre un pedido que no está en un estado válido.
     * @return ResponseEntity con el estado HTTP 400 y un mensaje detallado del error.
     */
    @ExceptionHandler(EstadoPedidoInvalido.class)
    public ResponseEntity<Map<String, Object>> handleEstadoPedidoInvalido(EstadoPedidoInvalido ex) {
        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
    /**
     * Maneja las excepciones de validación de argumentos.
     * @param ex La excepción lanzada cuando la validación de argumentos falla.
     * @return ResponseEntity con el estado HTTP 400 y un mensaje detallado de los errores de validación.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return buildError(HttpStatus.BAD_REQUEST, message);
    }
}
