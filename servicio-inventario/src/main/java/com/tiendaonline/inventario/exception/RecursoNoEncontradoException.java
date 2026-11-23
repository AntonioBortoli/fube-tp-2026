package com.tiendaonline.inventario.exception;

/** 
 * Excepción que se lanza cuando un recurso no es encontrado.
 */

public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
