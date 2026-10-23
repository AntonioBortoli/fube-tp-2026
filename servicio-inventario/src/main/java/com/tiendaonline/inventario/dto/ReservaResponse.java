package com.tiendaonline.inventario.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReservaResponse(
    UUID id,
    UUID productoId,
    UUID pedidoId,
    Integer cantidad,
    String estado,
    LocalDateTime fechaCreacion
) {}
