package com.tiendaonline.pedidos.exception;

/**
 * Excepción lanzada cuando se intenta realizar una operación no válida para el estado actual del pedido.
 */
public class EstadoPedidoInvalido extends RuntimeException {
    public EstadoPedidoInvalido(String mensaje) {
        super(mensaje);
    }
}
