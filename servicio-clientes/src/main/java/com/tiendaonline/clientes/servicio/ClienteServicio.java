package com.tiendaonline.clientes.servicio;

import com.tiendaonline.clientes.dto.ClienteConPedidosResponse;
import com.tiendaonline.clientes.dto.ClienteResponse;
import com.tiendaonline.clientes.dto.CrearClienteRequest;

import java.util.UUID;

public interface ClienteServicio {

    ClienteResponse registrarCliente(CrearClienteRequest request);

    ClienteResponse obtenerClientePorId(UUID id);

    ClienteConPedidosResponse obtenerClienteConPedidos(UUID id);
}
