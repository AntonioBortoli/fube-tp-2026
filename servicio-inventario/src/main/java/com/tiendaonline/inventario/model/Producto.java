package com.tiendaonline.inventario.model;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * Entidad que representa un producto en el inventario-
 * @param id
 * @param sku
 * @param nombre
 * @param cantidadDisponible
 * @version 1.0
 * 
 */

@Entity
@Table(name = "productos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder


public class Producto {
        /** Identificador único del producto (UUID). */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    
    @Column(nullable = false, unique = true, length = 64)
    private String sku;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "cantidad_disponible", nullable = false)
    private Integer cantidadDisponible;
    

}
