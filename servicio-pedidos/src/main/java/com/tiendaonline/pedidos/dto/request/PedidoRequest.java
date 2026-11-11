package com.tiendaonline.pedidos.dto.request;

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
 * DTO para la creación de un pedido.
 * Contiene el identificador del cliente y una lista de items que componen el pedido.
 * Cada item debe contener un productoId, cantidad y precioUnitario.
 * Se valida que el clienteId no sea nulo y que la lista de items no esté vacía.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoRequest {
    /**
     * Identificador del cliente que realiza el pedido.
     * Este campo es obligatorio y no puede ser nulo.
     */
    @NotNull(message = "El id del cliente es obligatorio")
    private UUID clienteId;
    /**
     * Lista de items que componen el pedido.
     * Cada item debe contener un productoId, cantidad y precioUnitario.
     * La lista no puede estar vacía.
     */
    @NotEmpty(message = "El pedido debe contener al menos un item")
    @Valid
    private List<ItemPedidoRequest> items;
}
