package com.tiendaonline.pedidos.exception;
/**
 * Excepción personalizada que se lanza cuando se intenta realizar una operación sobre un pedido que no está en un estado válido para dicha operación.
 */
public class EstadoPedidoInvalido extends RuntimeException {
    /**
     * Constructor que recibe un mensaje de error y lo pasa al constructor de la clase base RuntimeException.
     * @param mensaje El mensaje de error que describe la razón por la cual el estado del pedido es inválido.
     */
    public EstadoPedidoInvalido(String mensaje) {
        super(mensaje);
    }
}
