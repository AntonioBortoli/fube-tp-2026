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

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;

    @Transactional(readOnly = true)
    public PedidoResponse obtenerPorId(UUID id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new PedidoNoEncontrado(id));
        return mapearADto(pedido);
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> listarPorCliente(UUID clienteId) {
        return pedidoRepository.findByClienteId(clienteId)
                .stream()
                .map(this::mapearADto)
                .toList();
    }

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

    private ItemPedidoResponse mapearItemADto(ItemPedido item) {
        return ItemPedidoResponse.builder()
                .id(item.getId())
                .productoId(item.getProductoId())
                .cantidad(item.getCantidad())
                .precioUnitario(item.getPrecioUnitario())
                .build();
    }
}