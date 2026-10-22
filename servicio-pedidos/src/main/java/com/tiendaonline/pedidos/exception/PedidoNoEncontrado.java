package com.tiendaonline.pedidos.exception;

import java.util.UUID;
/**
 * Excepción personalizada que se lanza cuando un pedido no es encontrado en la base de datos.
 */
public class PedidoNoEncontrado extends RuntimeException {
    public PedidoNoEncontrado(UUID id) {
        super("Pedido no encontrado con id: " + id);
    }
}
