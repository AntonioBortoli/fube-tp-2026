package com.tiendaonline.inventario.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.UUID;

public record ReservaRequest(
    @NotNull 
    UUID productoId,
    @NotNull 
    UUID pedidoId,
    @NotNull @Positive
    Integer cantidad
) {}
