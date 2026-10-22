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
 * DTO para representar la respuesta de un Pedido.
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