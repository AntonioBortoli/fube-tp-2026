package com.tiendaonline.pedidos.repository;

import com.tiendaonline.pedidos.model.Pedido;
import com.tiendaonline.pedidos.model.enums.EstadoPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
/**
 * Repositorio para la entidad Pedido.
 * Proporciona métodos para realizar operaciones CRUD y consultas personalizadas en la base de datos.
 */
@Repository
public interface PedidoRepository extends JpaRepository<Pedido, UUID> {
    /**
     * Encuentra todos los pedidos asociados a un cliente específico.
     * @param clienteId El UUID del cliente.
     * @return Una lista de pedidos asociados al cliente.
     */
    List<Pedido> findByClienteId(UUID clienteId);

    /**
     * Encuentra todos los pedidos que tienen un estado específico y fueron creados antes de una fecha límite.
     * @param estado El estado del pedido.
     * @param fechaLimite La fecha límite para la creación del pedido.
     * @return Una lista de pedidos que cumplen con los criterios especificados.
     */
    List<Pedido> findByEstadoAndFechaCreacionBefore(EstadoPedido estado, LocalDateTime fechaLimite);
}
