package com.tiendaonline.inventario.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.UUID;



/** 
 * Clase que representa la solicitud de reserva.
 * @version 1.0
 * @param productoId ID del producto
 * @param pedidoId ID del pedido
 * @param cantidad Cantidad a reservar
 * 
*/

public record ReservaRequest(
    @NotNull 
    UUID productoId,
    @NotNull 
    UUID pedidoId,
    @NotNull @Positive
    Integer cantidad
) {}
