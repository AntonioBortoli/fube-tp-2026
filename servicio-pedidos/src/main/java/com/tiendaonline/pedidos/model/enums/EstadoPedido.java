package com.tiendaonline.pedidos.model.enums;
/**
 * Enum que representa los posibles estados de un pedido.
 * CREADO: El pedido ha sido creado pero aún no confirmado.
 * CONFIRMADO: El pedido ha sido confirmado y está en proceso.
 * CANCELADO: El pedido ha sido cancelado por el cliente o por el sistema.
 * EXPIRADO: El pedido ha expirado debido a la falta de confirmación en un tiempo determinado.
 */
public enum EstadoPedido {
    CREADO,
    CONFIRMADO,
    CANCELADO,
    EXPIRADO
}