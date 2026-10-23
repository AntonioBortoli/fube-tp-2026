package com.tiendaonline.clientes.mapper;

import com.tiendaonline.clientes.dto.ClienteResponse;
import com.tiendaonline.clientes.dto.CrearClienteRequest;
import com.tiendaonline.clientes.modelo.Cliente;



public class ClienteMapper {

    public static Cliente toEntity(CrearClienteRequest request) {
     if (request == null) return null;


        return Cliente.builder()
                .nombre(request.getNombre().trim())
                .email(request.getEmail().toString())
                .build();
    }

    public static ClienteResponse toResponse(Cliente cliente) {


        if (cliente == null) return null;

        return ClienteResponse.builder()
                .id(cliente.getId())
                .nombre(cliente.getNombre())
                .email(cliente.getEmail())
                .fechaAlta(cliente.getFechaAlta())
                .build();
    }


}
