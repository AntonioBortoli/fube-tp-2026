package com.tiendaonline.inventario.dto;

import java.util.UUID;

public record DisponibilidadResponse(
    UUID productoId,
    String sku,
    String nombre,
    Integer cantidadDisponible
) {}
