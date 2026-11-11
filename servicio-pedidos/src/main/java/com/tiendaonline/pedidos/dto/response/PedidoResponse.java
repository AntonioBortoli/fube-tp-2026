package com.tiendaonline.pedidos.dto.response;

import com.tiendaonline.pedidos.model.enums.EstadoPedido;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
/**
 * DTO para la respuesta de un pedido.
 * Contiene el identificador del pedido, el identificador del cliente, el estado del pedido,
 * el monto total, las fechas de creación y actualización, y la lista de items del pedido.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoResponse {
    private UUID id;
    private UUID clienteId;
    private EstadoPedido estado;
    private BigDecimal montoTotal;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private List<ItemPedidoResponse> items;
}
