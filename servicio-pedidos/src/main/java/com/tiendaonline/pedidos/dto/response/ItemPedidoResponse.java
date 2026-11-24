package com.tiendaonline.pedidos.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;
/**
 * DTO para la respuesta de un item de pedido.
 * Contiene el identificador del item, el identificador del producto, la cantidad y el precio unitario.
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
