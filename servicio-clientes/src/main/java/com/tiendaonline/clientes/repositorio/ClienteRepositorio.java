package com.tiendaonline.clientes.repositorio;


import com.tiendaonline.clientes.modelo.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository


public interface ClienteRepositorio extends JpaRepository<Cliente, UUID> {

    boolean existsByNombre(String email);

    Optional<Cliente> findByEmail(String email);
}
