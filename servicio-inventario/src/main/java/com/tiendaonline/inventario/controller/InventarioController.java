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

import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class InventarioController {

    private final InventarioService inventarioService;

    @GetMapping( "/productos/{id}/disponibilidad")
    public ResponseEntity<DisponibilidadResponse> consultarDisponibilidad(@PathVariable UUID id) {
       
        return ResponseEntity.ok(inventarioService.consultarDisponibilidad(id));
    }


    @PostMapping( "/reservas" )
    public ResponseEntity<ReservaResponse> reservarStock(@Valid @RequestBody ReservaRequest request) {
       
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inventarioService.reservarStock(request) );
    }



    @PatchMapping( "/reservas/{id}/liberar")
    public ResponseEntity<ReservaResponse> liberarReserva(@PathVariable UUID id) {
        
        return ResponseEntity.ok(inventarioService.liberarReserva(id) );
    }



}
