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


import java.time.LocalDateTime;
import java.util.UUID;


@Service
@RequiredArgsConstructor  
public class InventarioService {

    private final ProductoRepository productoRepository;
    private final ReservaRepository reservaRepository;

    public DisponibilidadResponse consultarDisponibilidad(UUID productoId) {
       
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
        
        return new ReservaResponse(
                reserva.getId(),                        
                reserva.getProducto().getId(),          
                reserva.getPedidoId(),                  
                reserva.getCantidad(),                  
                reserva.getEstado().name(),             
                reserva.getFechaCreacion()              
        );
    }



    @Transactional
    public ReservaResponse liberarReserva(UUID reservaId) {
         // Buscar la reserva por su ID
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Reserva no encontrada con ID: " + reservaId));


        //  Verificar que no estE ya liberada
        if (reserva.getEstado() == EstadoReserva.LIBERADA) {
            throw new StockInsuficienteException(
                    "La reserva con ID: " + reservaId + " ya fue liberada");
        }
        //  Cambiar el estada a LIBERADA
        reserva.setEstado(EstadoReserva.LIBERADA);
        //  Devolver la cantidad reservada al stock
        Producto producto = reserva.getProducto();
        producto.setCantidadDisponible(
                producto.getCantidadDisponible() + reserva.getCantidad()
        );

        //  Guardar los cambios
        productoRepository.save(producto);
        reservaRepository.save(reserva);
       
        return new ReservaResponse(
                reserva.getId(),
                producto.getId(),
                reserva.getPedidoId(),
                reserva.getCantidad(),
                reserva.getEstado().name(),             // "LIBERADA"
                reserva.getFechaCreacion()
        );
    }
}
