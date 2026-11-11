package com.tiendaonline.pedidos.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;
/**
 * DTO para representar un item de pedido en una solicitud.
 * Contiene información sobre el producto, la cantidad solicitada y el precio unitario.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemPedidoRequest {
    /**
     * Identificador del producto que se desea agregar al pedido.
     */
    @NotNull(message = "El id del producto es obligatorio")
    private UUID productoId;
    /**
     * Cantidad del producto que se desea agregar al pedido.
     * Debe ser un valor positivo mayor a 0.
     */
    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser mayor a 0")
    private Integer cantidad;
    /**
     * Precio unitario del producto en el momento de la creación del pedido.
     * Debe ser un valor mayor a 0.
     */
    @NotNull(message = "El precio unitario es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio unitario debe ser mayor a 0")
    private BigDecimal precioUnitario;
}
