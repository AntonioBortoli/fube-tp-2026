package com.tiendaonline.pedidos.repository;

import com.tiendaonline.pedidos.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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

}