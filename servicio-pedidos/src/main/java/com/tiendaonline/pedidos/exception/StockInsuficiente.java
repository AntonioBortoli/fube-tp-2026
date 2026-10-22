package com.tiendaonline.pedidos.exception;

import java.util.UUID;
/**
 * Excepción personalizada que se lanza cuando no hay suficiente stock para un producto.
 */
public class StockInsuficiente extends RuntimeException {
    /**
     * Constructor que recibe el UUID del producto y construye un mensaje de error indicando que no hay suficiente stock.
     * @param productoId El UUID del producto con stock insuficiente.
     */
    public StockInsuficiente(UUID productoId) {
        super("Stock insuficiente para el producto con id: " + productoId);
    }
}
