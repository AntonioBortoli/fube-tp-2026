package com.tiendaonline.pedidos.controller;

import com.tiendaonline.pedidos.dto.request.PedidoRequest;
import com.tiendaonline.pedidos.dto.response.PedidoResponse;
import com.tiendaonline.pedidos.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
/**
 * Controlador REST para la gestión de pedidos.
 * Proporciona endpoints para crear, consultar, listar y cancelar pedidos.
 * Utiliza el servicio PedidoService para realizar las operaciones de negocio.
 */
@RestController
@RequestMapping("/pedidos")
@RequiredArgsConstructor
@Tag(name = "Pedidos", description = "API de gestión de pedidos")
public class PedidoController {

    private final PedidoService pedidoService;
    /**
     * Endpoint para obtener un pedido por su identificador.
     * Si el pedido no existe, se lanza una excepción PedidoNoEncontrado.
     *
     * @param id Identificador del pedido a consultar.
     * @return ResponseEntity con el PedidoResponse correspondiente al pedido solicitado.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Consultar un pedido mediante su identificador")
    public ResponseEntity<PedidoResponse> obtenerPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(pedidoService.obtenerPorId(id));
    }
    /**
     * Endpoint para listar los pedidos de un cliente específico.
     * Si se proporciona un clienteId, se retornan los pedidos asociados a ese cliente.
     * Si no se proporciona un clienteId, se retorna una lista vacía.
     *
     * @param clienteId Identificador del cliente cuyos pedidos se desean listar (opcional).
     * @return ResponseEntity con la lista de PedidoResponse correspondientes al cliente.
     */
    @GetMapping
    @Operation(summary = "Listar los pedidos de un cliente")
    public ResponseEntity<List<PedidoResponse>> listarPedidos(@RequestParam(required = false) UUID clienteId) {
        if (clienteId != null) {
            return ResponseEntity.ok(pedidoService.listarPorCliente(clienteId));
        }
        return ResponseEntity.ok(List.of());
    }
    /**
     * Endpoint para crear un nuevo pedido.
     * Verifica que el cliente exista y que haya suficiente stock para los productos solicitados.
     * Si todo es correcto, crea el pedido en estado CREADO y reserva el stock correspondiente.
     *
     * @param request Objeto PedidoRequest que contiene los detalles del pedido a crear.
     * @return ResponseEntity con el PedidoResponse del pedido creado.
     */
    @PostMapping
    @Operation(summary = "Crear un nuevo pedido verificando cliente y reservando stock")
    public ResponseEntity<PedidoResponse> crearPedido(@Valid @RequestBody PedidoRequest request) {
        PedidoResponse response = pedidoService.crearPedido(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    /**
     * Endpoint para confirmar un pedido en estado CREADO.
     * Cambia el estado del pedido a CONFIRMADO y mantiene la reserva de stock.
     *
     * @param id Identificador del pedido a confirmar.
     * @return ResponseEntity con el PedidoResponse actualizado.
     */
    @PostMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar un pedido en estado CREADO o CONFIRMADO liberando la reserva de stock")
    public ResponseEntity<PedidoResponse> cancelarPedido(@PathVariable UUID id) {
        return ResponseEntity.ok(pedidoService.cancelarPedido(id));
    }
}
