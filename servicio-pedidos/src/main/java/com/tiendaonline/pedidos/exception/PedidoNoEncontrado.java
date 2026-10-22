package com.tiendaonline.pedidos.exception;

import java.util.UUID;

public class PedidoNoEncontrado extends RuntimeException {
    public PedidoNoEncontrado(UUID id) {
        super("Pedido no encontrado con id: " + id);
    }
}
