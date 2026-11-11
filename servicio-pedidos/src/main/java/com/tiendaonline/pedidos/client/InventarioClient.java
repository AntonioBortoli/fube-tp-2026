package com.tiendaonline.pedidos.client;

import com.tiendaonline.pedidos.dto.request.ReservaRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.UUID;

/**
 * Cliente REST para interactuar con el servicio de inventario.
 * Proporciona métodos para reservar y liberar stock de productos.
 */
@Component
@Slf4j
public class InventarioClient {

    private final RestClient restClient;

    public InventarioClient(
            RestClient.Builder builder,
            @Value("${servicios.inventario.url}") String baseUrl) {
        this.restClient = builder
                .baseUrl(baseUrl)
                .requestFactory(new JdkClientHttpRequestFactory())
                .build();
    }

    /**
     * Intenta reservar stock para un producto específico asociado a un pedido.
     *
     * @param productoId El ID del producto para el cual se desea reservar stock.
     * @param pedidoId   El ID del pedido asociado a la reserva.
     * @param cantidad   La cantidad de stock que se desea reservar.
     * @return true si la reserva fue exitosa (HTTP 2xx), false si fue rechazada o hubo error.
     */
    public boolean reservarStock(UUID productoId, UUID pedidoId, Integer cantidad) {
        try {
            String traceId = MDC.get("traceId");
            ReservaRequest request = ReservaRequest.builder()
                    .productoId(productoId)
                    .pedidoId(pedidoId)
                    .cantidad(cantidad)
                    .build();

            restClient.post()
                    .uri("/api/reservas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .headers(headers -> {
                        if (traceId != null && !traceId.isBlank()) {
                            headers.set("X-Correlation-ID", traceId);
                        }
                    })
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();
            return true;
        } catch (RestClientException e) {
            log.warn("No se pudo reservar stock para producto {} y pedido {}: {}", productoId, pedidoId, e.getMessage());
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
            String traceId = MDC.get("traceId");
            restClient.patch()
                    .uri("/api/reservas/pedido/{pedidoId}/liberar", pedidoId)
                    .headers(headers -> {
                        if (traceId != null && !traceId.isBlank()) {
                            headers.set("X-Correlation-ID", traceId);
                        }
                    })
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            log.warn("No se pudieron liberar reservas para el pedido {}: {}", pedidoId, e.getMessage());
        }
    }
}