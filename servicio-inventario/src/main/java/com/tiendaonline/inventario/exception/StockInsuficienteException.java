package com.tiendaonline.inventario.exception;
/**
 * Excepción que se lanza cuando no hay suficiente stock.
 */

public class StockInsuficienteException extends RuntimeException {
    public StockInsuficienteException(String mensaje) {
        super(mensaje);
    }
}
