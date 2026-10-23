package com.tiendaonline.clientes.servicio;

import com.tiendaonline.clientes.dto.ClienteResponse;
import com.tiendaonline.clientes.dto.CrearClienteRequest;
import com.tiendaonline.clientes.modelo.Cliente;

import java.util.UUID;

public interface ClienteServicio {

    ClienteResponse registrarCliente(CrearClienteRequest request);

    ClienteResponse obtenerClientePorId(UUID id);
}
