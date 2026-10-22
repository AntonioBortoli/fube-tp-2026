package com.tiendaonline.pedidos.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;
/**
 * DTO para representar la respuesta de un ItemPedido.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemPedidoResponse {
    private UUID id;
    private UUID productoId;
    private Integer cantidad;
    private BigDecimal precioUnitario;
}