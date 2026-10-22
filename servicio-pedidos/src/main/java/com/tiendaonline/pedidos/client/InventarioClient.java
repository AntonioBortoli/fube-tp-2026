package com.tiendaonline.pedidos.client;

import com.tiendaonline.pedidos.dto.request.ReservaRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;
/**
 * Cliente REST para interactuar con el servicio de inventario.
 * Proporciona métodos para reservar y liberar stock de productos.
 */
@Component
public class InventarioClient {

    private final RestClient restClient;
    /**
     * Constructor para inicializar el cliente REST con la URL base del servicio de inventario.
     *
     * @param baseUrl La URL base del servicio de inventario, inyectada desde las propiedades de configuración.
     */
    public InventarioClient(@Value("${servicios.inventario.url}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
    /**
     * Intenta reservar stock para un producto específico asociado a un pedido.
     *
     * @param productoId El ID del producto para el cual se desea reservar stock.
     * @param pedidoId   El ID del pedido asociado a la reserva.
     * @param cantidad   La cantidad de stock que se desea reservar.
     * @return true si la reserva fue exitosa, false si no se pudo realizar la reserva.
     */
    public boolean reservarStock(UUID productoId, UUID pedidoId, Integer cantidad) {
        try {
            ReservaRequest request = ReservaRequest.builder()
                    .productoId(productoId)
                    .pedidoId(pedidoId)
                    .cantidad(cantidad)
                    .build();

            restClient.post()
                    .uri("/reservas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (req, res) -> {
                        // Si da 400, 409 o 500 significa que no se pudo reservar
                    })
                    .toBodilessEntity();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    /**
     * Libera las reservas de stock asociadas a un pedido específico.
     *
     * @param pedidoId El ID del pedido cuyas reservas deben ser liberadas.
     */
    public void liberarReservaPorPedido(UUID pedidoId) {
        try {
            restClient.post()
                    .uri("/reservas/liberar-por-pedido/{pedidoId}", pedidoId)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception ignored) {
            // Se puede loguear advertencia
        }
    }
}