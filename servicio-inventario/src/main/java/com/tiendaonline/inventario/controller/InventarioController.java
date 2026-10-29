package com.tiendaonline.inventario.controller;


import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import com.tiendaonline.inventario.service.InventarioService;
import com.tiendaonline.inventario.dto.DisponibilidadResponse;
import com.tiendaonline.inventario.dto.ReservaRequest;
import com.tiendaonline.inventario.dto.ReservaResponse;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;


import java.util.UUID;

@Tag(name = "Inventario", description = "Operaciones de stock y reservas")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class InventarioController {

   

    private final InventarioService inventarioService;
    

    @Operation(summary = "Consultar stock disponible", description = "Retorna la cantidad disponible para un producto dado su UUID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto encontrado"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @GetMapping( "/productos/{id}/disponibilidad")
    public ResponseEntity<DisponibilidadResponse> consultarDisponibilidad(@PathVariable UUID id) {
        return ResponseEntity.ok(inventarioService.consultarDisponibilidad(id));
    }

    @Operation(summary = "Reservar stock", description = "Reserva stock de un producto para un pedido")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Reserva creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
        @ApiResponse(responseCode = "409", description = "Stock insuficiente")
    })

    @PostMapping( "/reservas")
    public ResponseEntity<ReservaResponse> reservarStock(@Valid @RequestBody ReservaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inventarioService.reservarStock(request));
    }


    @Operation(summary = "Liberar una reserva", description = "Libera stock previamente reservado")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reserva liberada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Reserva no encontrada"),
        @ApiResponse(responseCode = "409", description = "La reserva ya está liberada")
    })

    @PatchMapping("/reservas/{id}/liberar")
    public ResponseEntity<ReservaResponse> liberarReserva(@PathVariable UUID id) {
        return ResponseEntity.ok(inventarioService.liberarReserva(id));
    }


    @Operation(summary = "Liberar todas las reservas de un pedido", description = "Libera todas las reservas asociadas a un pedido específico")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reservas liberadas exitosamente"),
        @ApiResponse(responseCode = "404", description = "No se encontraron reservas para el pedido")
    })
    
    @PatchMapping("/reservas/pedido/{pedidoId}/liberar")
    public ResponseEntity<List<ReservaResponse>> liberarReservasPorPedido(@PathVariable UUID pedidoId) {
        return ResponseEntity.ok(inventarioService.liberarReservasPorPedido(pedidoId));
    }




}
