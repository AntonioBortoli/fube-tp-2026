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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)  
class InventarioServiceTest {

    @Mock  
    private ProductoRepository productoRepository;

    @Mock  
    private ReservaRepository reservaRepository;

    @InjectMocks  
    private InventarioService inventarioService;

  
    private Producto crearProducto(int stock) {
        Producto p = new Producto();
        p.setId(UUID.randomUUID());
        p.setSku("SKU-TEST-001");
        p.setNombre("Producto Test");
        p.setCantidadDisponible(stock);
        return p;
    }

  
    @Test
    void consultarDisponibilidad_productoExiste_debeRetornarResponse() {
        // (preparar)
        Producto producto = crearProducto(50);
        // Cuando el servicio llame a findById, devolvemos nuestro producto
        when(productoRepository.findById(producto.getId()))
                .thenReturn(Optional.of(producto));

        // (ejecutar)
        DisponibilidadResponse response = inventarioService
                .consultarDisponibilidad(producto.getId());

        // (verificar)
        assertEquals(producto.getId(), response.productoId());
        assertEquals("SKU-TEST-001", response.sku());
        assertEquals("Producto Test", response.nombre());
        assertEquals(50, response.cantidadDisponible());
    }

    @Test
    void consultarDisponibilidad_productoNoExiste_debeLanzarExcepcion() {
        UUID idInexistente = UUID.randomUUID();
        // findById devuelve vacio
        when(productoRepository.findById(idInexistente))
                .thenReturn(Optional.empty());

        // Verificamos que lanza la excepción correcta
        assertThrows(RecursoNoEncontradoException.class, () ->
                inventarioService.consultarDisponibilidad(idInexistente));
    
    
            }

     
    @Test
    void reservarStock_conStockSuficiente_debeCrearReserva() {
     
        Producto producto = crearProducto(100);
        ReservaRequest request = new ReservaRequest(
                producto.getId(), UUID.randomUUID(), 30);

        when(productoRepository.findById(producto.getId()))
                .thenReturn(Optional.of(producto));
       

        when(productoRepository.save(any(Producto.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        when(reservaRepository.save(any(Reserva.class)))
                .thenAnswer(inv -> {
                    Reserva r = inv.getArgument(0);
                    r.setId(UUID.randomUUID()); 
                    return r;
                });

        

        ReservaResponse response = inventarioService.reservarStock(request);

        
        assertEquals(30, response.cantidad());
        assertEquals("ACTIVA", response.estado());
        assertEquals(70, producto.getCantidadDisponible()); 

        verify(productoRepository).save(producto);
        verify(reservaRepository).save(any(Reserva.class));
    
    
    
    }

    @Test
    void reservarStock_sinStockSuficiente_debeLanzarExcepcion() {
        

        Producto producto = crearProducto(5);
        ReservaRequest request = new ReservaRequest(
                producto.getId(), UUID.randomUUID(), 50); 



        when(productoRepository.findById(producto.getId()))
                .thenReturn(Optional.of(producto));

    

        assertThrows(StockInsuficienteException.class, () ->
                inventarioService.reservarStock(request));


     
        verify(productoRepository, never()).save(any());
        verify(reservaRepository, never()).save(any());
    
    
    }




    @Test
    void liberarReserva_reservaActiva_debeVolverStock() {
      

        Producto producto = crearProducto(70);
        Reserva reserva = new Reserva();
        reserva.setId(UUID.randomUUID());
        reserva.setProducto(producto);
        reserva.setCantidad(30);
        reserva.setEstado(EstadoReserva.ACTIVA);
        reserva.setFechaCreacion(LocalDateTime.now());

        when(reservaRepository.findById(reserva.getId()))
                .thenReturn(Optional.of(reserva));
        when(productoRepository.save(any(Producto.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        when(reservaRepository.save(any(Reserva.class)))
                .thenAnswer(inv -> inv.getArgument(0));

    

        ReservaResponse response = inventarioService.liberarReserva(reserva.getId());

  
        assertEquals("LIBERADA", response.estado());
        assertEquals(100, producto.getCantidadDisponible()); 
        verify(productoRepository).save(producto);
        verify(reservaRepository).save(reserva);



    }

    @Test
    void liberarReserva_yaLiberada_debeLanzarExcepcion() {
     
        Reserva reserva = new Reserva();
        reserva.setId(UUID.randomUUID());
        reserva.setEstado(EstadoReserva.LIBERADA);

        when(reservaRepository.findById(reserva.getId()))
                .thenReturn(Optional.of(reserva));

        

        assertThrows(StockInsuficienteException.class, () ->
                inventarioService.liberarReserva(reserva.getId()));
    }



}
