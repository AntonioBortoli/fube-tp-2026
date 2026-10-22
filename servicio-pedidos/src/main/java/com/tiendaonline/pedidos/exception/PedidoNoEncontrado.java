package com.tiendaonline.pedidos.exception;

import java.util.UUID;
/**
 * Excepción personalizada que se lanza cuando un pedido no es encontrado en la base de datos.
 */
public class PedidoNoEncontrado extends RuntimeException {
    /**
     * Constructor que recibe el UUID del pedido no encontrado y construye un mensaje de error.
     * @param id El UUID del pedido que no fue encontrado.
     */
    public PedidoNoEncontrado(UUID id) {
        super("Pedido no encontrado con id: " + id);
    }
}
