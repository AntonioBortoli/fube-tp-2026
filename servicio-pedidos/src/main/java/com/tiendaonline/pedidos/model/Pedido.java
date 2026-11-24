package com.tiendaonline.pedidos.model;

import com.tiendaonline.pedidos.model.enums.EstadoPedido;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
/**
 * Entidad que representa un pedido.
 * Contiene el identificador del pedido, el identificador del cliente, el estado del pedido,
 * el monto total, las fechas de creación y actualización, y la lista de items del pedido.
 */
@Entity
@Table(name="pedidos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pedido {
    @Id
    private UUID id;

    @PrePersist
    public void asignarIdSiFalta() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
    }
    @Column(name="cliente_id",nullable = false)
    private UUID clienteId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPedido estado;

    @Column(name="monto_total",nullable = false,precision = 12, scale = 2)
    private BigDecimal montoTotal;

    @Column(name="fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name="fecha_actualizacion",nullable = false)
    private LocalDateTime fechaActualizacion;
    /**
     * Lista de items del pedido.
     */
    @Builder.Default
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval=true)
    private List<ItemPedido> items= new ArrayList<>();
    /**
     * Agrega un item al pedido y establece la relación bidireccional.
     * @param item El item de pedido a agregar.
     */
    public void agregarItem(ItemPedido item){
        this.items.add(item);
        item.setPedido(this);
    }

}
