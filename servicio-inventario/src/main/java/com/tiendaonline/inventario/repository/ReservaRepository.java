package com.tiendaonline.inventario.repository;

import com.tiendaonline.inventario.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ReservaRepository extends JpaRepository<Reserva, UUID> {
    List<Reserva> findByPedidoId(UUID pedidoId);
}
