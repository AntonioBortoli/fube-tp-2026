
package com.tiendaonline.pedidos.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;
/**
 * DTO para representar la solicitud de una Reserva.
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