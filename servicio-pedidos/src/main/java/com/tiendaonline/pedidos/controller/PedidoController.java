package com.tiendaonline.pedidos.controller;

import com.tiendaonline.pedidos.dto.request.PedidoRequest;
import com.tiendaonline.pedidos.dto.response.PedidoResponse;
import com.tiendaonline.pedidos.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
/**
 * Controlador REST para la gestión de pedidos.
 * Proporciona endpoints para consultar y listar pedidos.
 */
@RestController
@RequestMapping("/pedidos")
@RequiredArgsConstructor
@Tag(name = "Pedidos", description = "API de gestión de pedidos")
public class PedidoController {

    private final PedidoService pedidoService;
    /**
     * Endpoint para consultar un pedido mediante su identificador.
     * @param id Identificador del pedido.
     * @return Detalles del pedido.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Consultar un pedido mediante su identificador")
    public ResponseEntity<PedidoResponse> obtenerPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(pedidoService.obtenerPorId(id));
    }
    /**
     * Endpoint para listar los pedidos de un cliente.
     * @param clienteId Identificador del cliente (opcional).
     * @return Lista de pedidos del cliente o lista vacía si no se proporciona clienteId.
     */
    @GetMapping
    @Operation(summary = "Listar los pedidos de un cliente")
    public ResponseEntity<List<PedidoResponse>> listarPedidos(@RequestParam(required = false) UUID clienteId) {
        if (clienteId != null) {
            return ResponseEntity.ok(pedidoService.listarPorCliente(clienteId));
        }
        // Si no mandan clienteId podemos retornar lista vacía o implementar un listar todos si hiciera falta
        return ResponseEntity.ok(List.of());
    }
    /**
     * Endpoint para crear un nuevo pedido.
     * Verifica la existencia del cliente y reserva el stock de los productos.
     * @param request DTO con los detalles del pedido a crear.
     * @return Detalles del pedido creado.
     */
    @PostMapping
    @Operation(summary = "Crear un nuevo pedido verificando cliente y reservando stock")
    public ResponseEntity<PedidoResponse> crearPedido(@Valid @RequestBody PedidoRequest request) {
        PedidoResponse response = pedidoService.crearPedido(request);
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(response);
    }
}