package com.tiendaonline.inventario.dto;

import java.time.LocalDateTime;
import java.util.UUID;
/**
 * Clase que representa la respuesta de reserva. 
 * @version 1.0
 * @param id ID de la reserva
 * @param productoId ID del producto
 * @param pedidoId ID del pedido
 * @param cantidad Cantidad reservada
 * @param estado Estado de la reserva
 * @param fechaCreacion Fecha de creación
 * */


public record ReservaResponse(
    UUID id,
    UUID productoId,
    UUID pedidoId,
    Integer cantidad,
    String estado,
    LocalDateTime fechaCreacion
) {}
