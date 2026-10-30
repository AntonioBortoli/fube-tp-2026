package com.tiendaonline.clientes.controlador;

import com.tiendaonline.clientes.dto.ClienteResponse;
import com.tiendaonline.clientes.dto.CrearClienteRequest;
import com.tiendaonline.clientes.servicio.ClienteServicio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;



import java.net.URI;
import java.util.UUID;



@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
@Tag(name= "Clientes", description= "API para la gestión de clientes")



public class ClienteControlador {
    
    private final ClienteServicio servicio;
    
    
    @PostMapping
    @Operation(summary= "Registrar un nuevo cliente")
    public ResponseEntity<ClienteResponse> registrarCliente(@Valid @RequestBody CrearClienteRequest crearClienteRequest){
        ClienteResponse clienteCreado= servicio.registrarCliente(crearClienteRequest);
        
        URI location= ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(clienteCreado.getId())
                .toUri();
        
        

        return ResponseEntity.created(location).body(clienteCreado);
    }


    @GetMapping("/{id}")
    @Operation(summary = "Consultar datos de un cliente por ID")
    public ResponseEntity<ClienteResponse> obtenerCLientePorId(@PathVariable UUID id){
        ClienteResponse cliente= servicio.obtenerClientePorId(id);

        return ResponseEntity.ok(cliente);
    }



}
