package com.tiendaonline.pedidos.service;

import com.tiendaonline.pedidos.dto.PedidoResponse;
import com.tiendaonline.pedidos.exception.PedidoNoEncontrado;
import com.tiendaonline.pedidos.model.Pedido;
import com.tiendaonline.pedidos.model.enums.EstadoPedido;
import com.tiendaonline.pedidos.repository.PedidoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
/**
 * Pruebas unitarias para la clase PedidoService.
 */
@ExtendWith(MockitoExtension.class)
public class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @InjectMocks
    private PedidoService pedidoService;
    /**
     * Prueba unitaria para el método obtenerPorId de PedidoService.
     * Verifica que cuando se proporciona un ID válido, se retorna un PedidoResponse correspondiente.
     */
    @Test
    void obtenerPorId_cuandoExiste_retornaPedidoResponse() {
        UUID id = UUID.randomUUID();
        Pedido pedido = Pedido.builder()
                .id(id)
                .clienteId(UUID.randomUUID())
                .estado(EstadoPedido.CREADO)
                .montoTotal(new BigDecimal("1500.00"))
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();

        when(pedidoRepository.findById(id)).thenReturn(Optional.of(pedido));

        PedidoResponse response = pedidoService.obtenerPorId(id);

        assertNotNull(response);
        assertEquals(id, response.getId());
        assertEquals(EstadoPedido.CREADO, response.getEstado());
        verify(pedidoRepository, times(1)).findById(id);
    }
    /**
     * Prueba unitaria para el método obtenerPorId de PedidoService.
     * Verifica que cuando se proporciona un ID que no existe, se lanza la excepción PedidoNoEncontrado.
     */
    @Test
    void obtenerPorId_cuandoNoExiste_lanzaExcepcion() {
        UUID id = UUID.randomUUID();
        when(pedidoRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(PedidoNoEncontrado.class, () -> pedidoService.obtenerPorId(id));
        verify(pedidoRepository, times(1)).findById(id);
    }
    /**
     * Prueba unitaria para el método listarPorCliente de PedidoService.
     * Verifica que cuando se proporciona un clienteId válido, se retorna una lista de PedidoResponse correspondiente.
     */
    @Test
    void listarPorClienteId_retornaListaDePedidos() {
        UUID clienteId = UUID.randomUUID();
        Pedido pedido1 = Pedido.builder()
                .id(UUID.randomUUID())
                .clienteId(clienteId)
                .estado(EstadoPedido.CREADO)
                .montoTotal(new BigDecimal("1000.00"))
                .items(new ArrayList<>())
                .build();

        when(pedidoRepository.findByClienteId(clienteId)).thenReturn(List.of(pedido1));

        List<PedidoResponse> pedidos = pedidoService.listarPorCliente(clienteId);

        assertNotNull(pedidos);
        assertEquals(1, pedidos.size());
        assertEquals(clienteId, pedidos.get(0).getClienteId());
        verify(pedidoRepository, times(1)).findByClienteId(clienteId);
    }
}