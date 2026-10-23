package com.tiendaonline.clientes.servicio;

import com.tiendaonline.clientes.dto.ClienteResponse;
import com.tiendaonline.clientes.dto.CrearClienteRequest;
import com.tiendaonline.clientes.excepcion.EmailDuplicadoException;
import com.tiendaonline.clientes.excepcion.RecursoNoEncontradoException;
import com.tiendaonline.clientes.mapper.ClienteMapper;
import com.tiendaonline.clientes.modelo.Cliente;
import com.tiendaonline.clientes.repositorio.ClienteRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Service
@RequiredArgsConstructor

public class ClienteServicioImp implements ClienteServicio {

    private final ClienteRepositorio repositorio;

    @Override
    @Transactional
    public ClienteResponse registrarCliente(CrearClienteRequest request) {

        if (repositorio.existsByEmail(request.getEmail().trim().toLowerCase())) {
            throw new EmailDuplicadoException("Ya existe un cliente registrado con el Email: " + request.getEmail());
        }

        Cliente nuevoCliente = ClienteMapper.toEntity(request);

        Cliente clienteGuardado = repositorio.save(nuevoCliente);

        return ClienteMapper.toResponse(clienteGuardado);
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponse obtenerClientePorId(UUID id) {

        Cliente cliente = repositorio.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("No se encontró cliente con el ID: " + id));

        return ClienteMapper.toResponse(cliente);
    }




}
