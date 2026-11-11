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
 * Proporciona métodos para acceder y manipular los datos de pedidos en la base de datos.
 */
@Repository
public interface PedidoRepository extends JpaRepository<Pedido, UUID> {
    /**
     * Encuentra todos los pedidos asociados a un cliente específico.
     * @param clienteId El UUID del cliente.
     * @return Lista de pedidos del cliente.
     */
    List<Pedido> findByClienteId(UUID clienteId);
    /**
     * Encuentra todos los pedidos que están en un estado específico y fueron creados antes de una fecha límite.
     * @param estado El estado del pedido (por ejemplo, CREADO, CONFIRMADO, etc.).
     * @param fechaLimite La fecha límite para la creación del pedido.
     * @return Lista de pedidos que cumplen con los criterios.
     */
    List<Pedido> findByEstadoAndFechaCreacionBefore(EstadoPedido estado, LocalDateTime fechaLimite);
}
