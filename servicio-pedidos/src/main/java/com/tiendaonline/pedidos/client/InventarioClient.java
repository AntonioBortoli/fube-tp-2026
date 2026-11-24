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
 */
@Component
@Slf4j
public class InventarioClient {

    private final RestClient restClient;
    /**
     * Constructor para inicializar el cliente REST con la URL base del servicio de inventario.
     *
     * @param builder  El constructor de RestClient.
     * @param baseUrl  La URL base del servicio de inventario, inyectada desde las propiedades de configuración.
     */
    public InventarioClient(
            RestClient.Builder builder,
            @Value("${servicios.inventario.url}") String baseUrl) {
        this.restClient = builder
                .baseUrl(baseUrl)
                .requestFactory(new JdkClientHttpRequestFactory())
                .build();
    }
    /**
     * Intenta reservar stock para un producto específico en un pedido.
     *
     * @param productoId El UUID del producto para el cual se desea reservar stock.
     * @param pedidoId   El UUID del pedido asociado a la reserva.
     * @param cantidad   La cantidad de stock a reservar.
     * @return true si la reserva fue exitosa, false en caso contrario.
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
     * Intenta liberar la reserva de stock asociada a un pedido específico.
     *
     * @param pedidoId El UUID del pedido para el cual se desea liberar la reserva de stock.
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