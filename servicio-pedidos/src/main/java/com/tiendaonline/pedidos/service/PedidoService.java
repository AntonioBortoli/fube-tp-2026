package com.tiendaonline.pedidos.service;

import com.tiendaonline.pedidos.dto.ItemPedidoResponse;
import com.tiendaonline.pedidos.dto.PedidoResponse;
import com.tiendaonline.pedidos.exception.PedidoNoEncontrado;
import com.tiendaonline.pedidos.model.ItemPedido;
import com.tiendaonline.pedidos.model.Pedido;
import com.tiendaonline.pedidos.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
/**
 * Servicio para manejar la lógica de negocio relacionada con los pedidos.
 * Proporciona métodos para obtener pedidos por ID y listar pedidos por cliente.
 */
@Service
@RequiredArgsConstructor
public class PedidoService {

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
}