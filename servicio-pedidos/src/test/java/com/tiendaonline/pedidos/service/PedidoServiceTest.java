package com.tiendaonline.pedidos.service;

import com.tiendaonline.pedidos.client.ClientesClient;
import com.tiendaonline.pedidos.client.InventarioClient;
import com.tiendaonline.pedidos.dto.ItemPedidoRequest;
import com.tiendaonline.pedidos.dto.request.PedidoRequest;
import com.tiendaonline.pedidos.dto.response.PedidoResponse;
import com.tiendaonline.pedidos.exception.ClienteNoEncontrado;
import com.tiendaonline.pedidos.exception.PedidoNoEncontrado;
import com.tiendaonline.pedidos.exception.StockInsuficiente;
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
    @Mock
    private ClientesClient clientesClient;
    @Mock
    private InventarioClient inventarioClient;

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
    /**
     * Prueba unitaria para el método crearPedido de PedidoService.
     * Verifica que cuando el cliente existe y hay stock suficiente, se crea el pedido exitosamente.
     */
    @Test
    void crearPedido_cuandoClienteExisteYHayStock_creaPedidoExitosamente() {
        // Arrange
        UUID clienteId = UUID.randomUUID();
        UUID productoId = UUID.randomUUID();

        PedidoRequest request = PedidoRequest.builder()
                .clienteId(clienteId)
                .items(List.of(
                        ItemPedidoRequest.builder()
                                .productoId(productoId)
                                .cantidad(2)
                                .precioUnitario(new BigDecimal("100.00"))
                                .build()
                ))
                .build();

        when(clientesClient.existeCliente(clienteId)).thenReturn(true);
        when(inventarioClient.reservarStock(eq(productoId), any(UUID.class), eq(2))).thenReturn(true);
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> {
            Pedido p = invocation.getArgument(0);
            p.setId(UUID.randomUUID());
            return p;
        });

        // Act
        PedidoResponse response = pedidoService.crearPedido(request);

        // Assert
        assertNotNull(response);
        assertEquals(clienteId, response.getClienteId());
        assertEquals(EstadoPedido.CREADO, response.getEstado());
        // 2 unidades * $100.00 = $200.00
        assertEquals(new BigDecimal("200.00"), response.getMontoTotal());
        assertEquals(1, response.getItems().size());
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
    }
    /**
     * Prueba unitaria para el método crearPedido de PedidoService.
     * Verifica que cuando el cliente no existe, se lanza la excepción ClienteNoEncontrado.
     */
    @Test
    void crearPedido_cuandoClienteNoExiste_lanzaExcepcion() {
        // Arrange
        UUID clienteId = UUID.randomUUID();
        PedidoRequest request = PedidoRequest.builder()
                .clienteId(clienteId)
                .items(List.of(
                        ItemPedidoRequest.builder()
                                .productoId(UUID.randomUUID())
                                .cantidad(1)
                                .precioUnitario(new BigDecimal("50.00"))
                                .build()
                ))
                .build();

        when(clientesClient.existeCliente(clienteId)).thenReturn(false);

        // Act & Assert
        assertThrows(ClienteNoEncontrado.class, () -> pedidoService.crearPedido(request));
        verify(pedidoRepository, never()).save(any(Pedido.class));
        verify(inventarioClient, never()).reservarStock(any(), any(), any());
    }
    /**
     * Prueba unitaria para el método crearPedido de PedidoService.
     * Verifica que cuando no hay stock suficiente para un producto, se lanza la excepción StockInsuficiente.
     */
    @Test
    void crearPedido_cuandoNoHayStock_lanzaExcepcion() {
        // Arrange
        UUID clienteId = UUID.randomUUID();
        UUID productoId = UUID.randomUUID();
        PedidoRequest request = PedidoRequest.builder()
                .clienteId(clienteId)
                .items(List.of(
                        ItemPedidoRequest.builder()
                                .productoId(productoId)
                                .cantidad(5)
                                .precioUnitario(new BigDecimal("20.00"))
                                .build()
                ))
                .build();

        when(clientesClient.existeCliente(clienteId)).thenReturn(true);
        when(inventarioClient.reservarStock(eq(productoId), any(UUID.class), eq(5))).thenReturn(false);

        // Act & Assert
        assertThrows(StockInsuficiente.class, () -> pedidoService.crearPedido(request));
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }
}