package com.tiendaonline.inventario.service;


import com.tiendaonline.inventario.dto.DisponibilidadResponse;
import com.tiendaonline.inventario.dto.ReservaRequest;
import com.tiendaonline.inventario.dto.ReservaResponse;
import com.tiendaonline.inventario.exception.RecursoNoEncontradoException;
import com.tiendaonline.inventario.exception.StockInsuficienteException;
import com.tiendaonline.inventario.model.EstadoReserva;
import com.tiendaonline.inventario.model.Producto;
import com.tiendaonline.inventario.model.Reserva;
import com.tiendaonline.inventario.repository.ProductoRepository;
import com.tiendaonline.inventario.repository.ReservaRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor  
public class InventarioService {

    private final ProductoRepository productoRepository;
    private final ReservaRepository reservaRepository;

    public DisponibilidadResponse consultarDisponibilidad(UUID productoId) {
       

        log.info("Consultando disponibilidad para producto ID: {}", productoId);


        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Producto no encontrado con ID: " + productoId));

        return new DisponibilidadResponse(
                producto.getId(),
                producto.getSku(),
                producto.getNombre(),
                producto.getCantidadDisponible() 
        );           
    }


    @Transactional
    public ReservaResponse reservarStock(ReservaRequest request) {

         log.info("Iniciando reserva de stock: productoId={}, pedidoId={}, cantidad={}",
                request.productoId(), request.pedidoId(), request.cantidad());

        // Buscar el producto que se quiere reservar
        Producto producto = productoRepository.findById(request.productoId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Producto no encontrado con ID: " + request.productoId()));
        
       
        // Verificar que hay suficiente stocK
        if (producto.getCantidadDisponible() < request.cantidad()) {
            throw new StockInsuficienteException(
                    "Stock insuficiente para el producto '" + producto.getNombre()
                    + "'. Disponible: " + producto.getCantidadDisponible()
                    + ", solicitado: " + request.cantidad());
        }
        
        // Descontar la cantidad del stock
        producto.setCantidadDisponible(
                producto.getCantidadDisponible() - request.cantidad()
        );
        productoRepository.save(producto);  


        // Crear la nueva reserva
        Reserva reserva = new Reserva();
        reserva.setProducto(producto);
        reserva.setPedidoId(request.pedidoId());
        reserva.setCantidad(request.cantidad());
        reserva.setEstado(EstadoReserva.ACTIVA);
        reserva.setFechaCreacion(LocalDateTime.now());
        reservaRepository.save(reserva);  // Guardar la reserva en la DB
        

        ReservaResponse response = new ReservaResponse(
                reserva.getId(),                        
                reserva.getProducto().getId(),          
                reserva.getPedidoId(),                  
                reserva.getCantidad(),                  
                reserva.getEstado().name(),             
                reserva.getFechaCreacion()              
        );


        log.info("Reserva exitosa ID: {}. Stock restante: {}", reserva.getId(), producto.getCantidadDisponible());
        return response;

    }




        @Transactional
    public ReservaResponse liberarReserva(UUID reservaId) {
        log.info("Liberando reserva ID: {}", reservaId);
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Reserva no encontrada con ID: " + reservaId));

        if (reserva.getEstado() == EstadoReserva.LIBERADA) {
            throw new StockInsuficienteException(
                    "La reserva con ID: " + reservaId + " ya fue liberada");
        }



        reserva.setEstado(EstadoReserva.LIBERADA);
        Producto producto = reserva.getProducto();
        producto.setCantidadDisponible(
                producto.getCantidadDisponible() + reserva.getCantidad()
        );



        productoRepository.save(producto);
        reservaRepository.save(reserva);

        log.info("Reserva ID: {} liberada. Nuevo stock: {}", reservaId, producto.getCantidadDisponible());

        return new ReservaResponse(
                reserva.getId(),
                producto.getId(),
                reserva.getPedidoId(),
                reserva.getCantidad(),
                reserva.getEstado().name(),
                reserva.getFechaCreacion()
        );
        
    }



    @Transactional
    public List<ReservaResponse> liberarReservasPorPedido(UUID pedidoId) {
        
        
        log.info("Liberando todas las reservas para el pedido ID: {}", pedidoId);
        List<Reserva> reservas = reservaRepository.findByPedidoId(pedidoId);

        if (reservas.isEmpty()) {
            throw new RecursoNoEncontradoException("No se encontraron reservas para el pedido: " + pedidoId);
        }



        List<ReservaResponse> respuestas = new ArrayList<>();
        for (Reserva reserva : reservas) {
            if (reserva.getEstado() == EstadoReserva.ACTIVA) {
                reserva.setEstado(EstadoReserva.LIBERADA);
                Producto producto = reserva.getProducto();
                producto.setCantidadDisponible(producto.getCantidadDisponible() + reserva.getCantidad());
                productoRepository.save(producto);
                reservaRepository.save(reserva);
            }
            respuestas.add(new ReservaResponse(
                    reserva.getId(),
                    reserva.getProducto().getId(),
                    reserva.getPedidoId(),
                    reserva.getCantidad(),
                    reserva.getEstado().name(),
                    reserva.getFechaCreacion()
            ));
        }


        return respuestas;
    }

    
}
