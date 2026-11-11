package com.tiendaonline.pedidos.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;
/**
 * DTO para la solicitud de reserva de stock de un producto en un pedido.
 * Contiene el identificador del producto, el identificador del pedido y la cantidad a reservar.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservaRequest {

    private UUID productoId;

    private UUID pedidoId;

    private Integer cantidad;
}
