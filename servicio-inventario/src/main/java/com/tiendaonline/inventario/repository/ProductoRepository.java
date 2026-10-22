package com.tiendaonline.inventario.repository;

import com.tiendaonline.inventario.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ProductoRepository extends JpaRepository<Producto, UUID> {
    // Spring Data JPA ya te da: findById, findAll, save, deleteById, etc.
}
