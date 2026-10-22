package com.tiendaonline.pedidos.dto.request;

import com.tiendaonline.pedidos.dto.ItemPedidoRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;
/**
 * DTO para representar la solicitud de un Pedido.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoRequest {

    @NotNull(message = "El id del cliente es obligatorio")
    private UUID clienteId;

    @NotEmpty(message = "El pedido debe contener al menos un item")
    @Valid // Para que Spring valide también cada elemento de la lista
    private List<ItemPedidoRequest> items;
}