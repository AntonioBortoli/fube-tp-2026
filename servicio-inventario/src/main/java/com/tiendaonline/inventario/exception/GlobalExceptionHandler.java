package com.tiendaonline.inventario.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(RecursoNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage()));
    }



    @ExceptionHandler(StockInsuficienteException.class)
    public ResponseEntity<Map<String, String>> handleStockInsuficiente(StockInsuficienteException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)  // 409
                .body(Map.of("error", ex.getMessage()));
    }



    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        //  map para guardar los errores
        Map<String, String> errores = new HashMap<>();


        // Recorremos cada error de validacion
        ex.getBindingResult().getFieldErrors().forEach(error ->
            errores.put(error.getField(), error.getDefaultMessage())
        );


        return ResponseEntity.status(HttpStatus.BAD_REQUEST)  // 400
                .body(errores);
    }


    
}
