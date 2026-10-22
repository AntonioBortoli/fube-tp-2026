package com.tiendaonline.pedidos.service;

import com.tiendaonline.pedidos.client.ClientesClient;
import com.tiendaonline.pedidos.client.InventarioClient;
import com.tiendaonline.pedidos.dto.ItemPedidoRequest;
import com.tiendaonline.pedidos.dto.request.PedidoRequest;
import com.tiendaonline.pedidos.dto.response.ItemPedidoResponse;
import com.tiendaonline.pedidos.dto.response.PedidoResponse;
import com.tiendaonline.pedidos.exception.ClienteNoEncontrado;
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
 * Servicio para manejar la lógica de negocio relacionada con los pedidos.
 * Proporciona métodos para obtener pedidos por ID y listar pedidos por cliente.
 */
@Service
@RequiredArgsConstructor
public class PedidoService {
    private final ClientesClient clientesClient;
    private final InventarioClient inventarioClient;
    private final PedidoRepository pedidoRepository;
    /**
     * Obtiene un pedido por su identificador.
     * @param id Identificador del pedido.
     * @return DTO con los detalles del pedido.
     * @throws PedidoNoEncontrado si no se encuentra el pedido con el ID proporcionado.
     */
    @Transactional(readOnly = true)
    public PedidoResponse obtenerPorId(UUID id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new PedidoNoEncontrado(id));
        return mapearADto(pedido);
    }
    /**
     * Lista todos los pedidos asociados a un cliente específico.
     * @param clienteId Identificador del cliente.
     * @return Lista de DTOs con los detalles de los pedidos del cliente.
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
     * @param pedido Entidad Pedido a mapear.
     * @return DTO PedidoResponse con los datos del pedido.
     */
    // Método privado para transformar la Entidad JPA al DTO de salida
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
     * @param item Entidad ItemPedido a mapear.
     * @return DTO ItemPedidoResponse con los datos del item del pedido.
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
     * Crea un nuevo pedido.
     * Verifica la existencia del cliente y reserva el stock de los productos.
     * @param request DTO con los detalles del pedido a crear.
     * @return DTO con los detalles del pedido creado.
     * @throws ClienteNoEncontrado si el cliente no existe.
     * @throws StockInsuficiente si no hay suficiente stock para algún producto.
     */
    @Transactional
    public PedidoResponse crearPedido(PedidoRequest request) {
        // 1. Verificar si el cliente existe
        if (!clientesClient.existeCliente(request.getClienteId())) {
            throw new ClienteNoEncontrado(request.getClienteId());
        }

        // Generamos el ID del pedido con anticipación para asociarlo a las reservas
        UUID pedidoId = UUID.randomUUID();
        LocalDateTime ahora = LocalDateTime.now();

        // 2. Calcular monto total y preparar los items
        BigDecimal montoTotal = BigDecimal.ZERO;
        List<ItemPedido> items = new ArrayList<>();

        for (ItemPedidoRequest itemReq : request.getItems()) {
            // Reservar stock contra servicio-inventario
            boolean reservado = inventarioClient.reservarStock(
                    itemReq.getProductoId(),
                    pedidoId,
                    itemReq.getCantidad()
            );

            if (!reservado) {
                // Compensación: liberar reservas que hayan tenido éxito en este mismo pedido
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

        // 3. Construir y persistir la entidad Pedido
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
}