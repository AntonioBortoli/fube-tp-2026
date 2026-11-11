package com.tiendaonline.pedidos.exception;

import java.util.UUID;
/**
 * Excepción personalizada que se lanza cuando no hay suficiente stock para un producto.
 */
public class StockInsuficiente extends RuntimeException {
    /**
     * Constructor que recibe el UUID del producto con stock insuficiente y construye un mensaje de error.
     * @param productoId El UUID del producto que no tiene suficiente stock.
     */
    public StockInsuficiente(UUID productoId) {
        super("Stock insuficiente para el producto con id: " + productoId);
    }
}
