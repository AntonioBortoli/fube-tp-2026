package com.tiendaonline.inventario.dto;

import java.util.UUID;


/**
 * Clase que representa la respuesta de disponibilidad.
 * @version 1.0
 * @param productoId ID del producto
 * @param sku Código SKU
 * @param nombre Nombre del producto
 * @param cantidadDisponible Cantidad disponible
 * 
 */



public record DisponibilidadResponse(
    UUID productoId,
    String sku,
    String nombre,
    Integer cantidadDisponible
) {}
