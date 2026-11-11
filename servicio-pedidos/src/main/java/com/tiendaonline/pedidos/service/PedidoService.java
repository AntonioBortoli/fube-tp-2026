package com.tiendaonline.pedidos.service;

import com.tiendaonline.pedidos.client.ClientesClient;
import com.tiendaonline.pedidos.client.InventarioClient;
import com.tiendaonline.pedidos.dto.request.ItemPedidoRequest;
import com.tiendaonline.pedidos.dto.request.PedidoRequest;
import com.tiendaonline.pedidos.dto.response.ItemPedidoResponse;
import com.tiendaonline.pedidos.dto.response.PedidoResponse;
import com.tiendaonline.pedidos.exception.ClienteNoEncontrado;
import com.tiendaonline.pedidos.exception.EstadoPedidoInvalido;
import com.tiendaonline.pedidos.exception.PedidoNoEncontrado;
import com.tiendaonline.pedidos.exception.StockInsuficiente;
import com.tiendaonline.pedidos.model.ItemPedido;
import com.tiendaonline.pedidos.model.Pedido;
import com.tiendaonline.pedidos.model.enums.EstadoPedido;
import com.tiendaonline.pedidos.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
/**
 * Servicio para la gestión de pedidos.
 * Proporciona métodos para crear, obtener y cancelar pedidos, así como para listar pedidos por cliente.
 */
@Service
@RequiredArgsConstructor
public class PedidoService {
    private final ClientesClient clientesClient;
    private final InventarioClient inventarioClient;
    private final PedidoRepository pedidoRepository;
    /**
     * Obtiene un pedido por su identificador.
     * Si el pedido no existe, lanza una excepción PedidoNoEncontrado.
     *
     * @param id Identificador del pedido a consultar.
     * @return PedidoResponse correspondiente al pedido solicitado.
     */
    @Transactional(readOnly = true)
    public PedidoResponse obtenerPorId(UUID id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new PedidoNoEncontrado(id));
        return mapearADto(pedido);
    }
    /**
     * Lista los pedidos de un cliente específico.
     * Si el cliente no tiene pedidos, retorna una lista vacía.
     *
     * @param clienteId Identificador del cliente cuyos pedidos se desean listar.
     * @return Lista de PedidoResponse correspondientes al cliente.
     */
    @Transactional(readOnly = true)
    public List<PedidoResponse> listarPorCliente(UUID clienteId) {
        return pedidoRepository.findByClienteId(clienteId)
                .stream()
                .map(this::mapearADto)
                .toList();
    }
    /**
     * Mapea una entidad Pedido a su correspondiente DTO PedidoResponse.
     *
     * @param pedido Entidad Pedido a mapear.
     * @return DTO PedidoResponse correspondiente al pedido.
     */
    private PedidoResponse mapearADto(Pedido pedido) {
        List<ItemPedidoResponse> itemsDto = (pedido.getItems() != null)
                ? pedido.getItems().stream()
                .map(this::mapearItemADto)
                .toList()
                : List.of();

        return PedidoResponse.builder()
                .id(pedido.getId())
                .clienteId(pedido.getClienteId())
                .estado(pedido.getEstado())
                .montoTotal(pedido.getMontoTotal())
                .fechaCreacion(pedido.getFechaCreacion())
                .fechaActualizacion(pedido.getFechaActualizacion())
                .items(itemsDto)
                .build();
    }
    /**
     * Mapea una entidad ItemPedido a su correspondiente DTO ItemPedidoResponse.
     *
     * @param item Entidad ItemPedido a mapear.
     * @return DTO ItemPedidoResponse correspondiente al item del pedido.
     */
    private ItemPedidoResponse mapearItemADto(ItemPedido item) {
        return ItemPedidoResponse.builder()
                .id(item.getId())
                .productoId(item.getProductoId())
                .cantidad(item.getCantidad())
                .precioUnitario(item.getPrecioUnitario())
                .build();
    }
    /**
     * Crea un nuevo pedido verificando que el cliente exista y que haya suficiente stock para los productos solicitados.
     * Si todo es correcto, crea el pedido en estado CREADO y reserva el stock correspondiente.
     *
     * @param request Objeto PedidoRequest que contiene los detalles del pedido a crear.
     * @return PedidoResponse del pedido creado.
     */
    @Transactional
    public PedidoResponse crearPedido(PedidoRequest request) {
        if (!clientesClient.existeCliente(request.getClienteId())) {
            throw new ClienteNoEncontrado(request.getClienteId());
        }

        UUID pedidoId = UUID.randomUUID();
        LocalDateTime ahora = LocalDateTime.now();

        BigDecimal montoTotal = BigDecimal.ZERO;
        List<ItemPedido> items = new ArrayList<>();

        for (ItemPedidoRequest itemReq : request.getItems()) {
            boolean reservado = inventarioClient.reservarStock(
                    itemReq.getProductoId(),
                    pedidoId,
                    itemReq.getCantidad()
            );

            if (!reservado) {
                inventarioClient.liberarReservaPorPedido(pedidoId);
                throw new StockInsuficiente(itemReq.getProductoId());
            }

            BigDecimal subtotal = itemReq.getPrecioUnitario()
                    .multiply(BigDecimal.valueOf(itemReq.getCantidad()));
            montoTotal = montoTotal.add(subtotal);

            ItemPedido item = ItemPedido.builder()
                    .productoId(itemReq.getProductoId())
                    .cantidad(itemReq.getCantidad())
                    .precioUnitario(itemReq.getPrecioUnitario())
                    .build();

            items.add(item);
        }

        Pedido pedido = Pedido.builder()
                .id(pedidoId)
                .clienteId(request.getClienteId())
                .estado(EstadoPedido.CREADO)
                .montoTotal(montoTotal)
                .fechaCreacion(ahora)
                .fechaActualizacion(ahora)
                .build();

        for (ItemPedido item : items) {
            pedido.agregarItem(item);
        }

        Pedido pedidoGuardado = pedidoRepository.save(pedido);
        return mapearADto(pedidoGuardado);
    }
    /**
     * Cancela un pedido en estado CREADO o CONFIRMADO liberando la reserva de stock.
     * Cambia el estado del pedido a CANCELADO y actualiza la fecha de actualización.
     *
     * @param id Identificador del pedido a cancelar.
     * @return PedidoResponse actualizado después de la cancelación.
     */
    @Transactional
    public PedidoResponse cancelarPedido(UUID id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new PedidoNoEncontrado(id));

        if (pedido.getEstado() != EstadoPedido.CREADO && pedido.getEstado() != EstadoPedido.CONFIRMADO) {
            throw new EstadoPedidoInvalido("No se puede cancelar un pedido en estado: " + pedido.getEstado());
        }

        inventarioClient.liberarReservaPorPedido(pedido.getId());

        pedido.setEstado(EstadoPedido.CANCELADO);
        pedido.setFechaActualizacion(LocalDateTime.now());

        return mapearADto(pedidoRepository.save(pedido));
    }
}