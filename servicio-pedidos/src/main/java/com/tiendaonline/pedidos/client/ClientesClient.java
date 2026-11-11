package com.tiendaonline.pedidos.client;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.UUID;

/**
 * Cliente REST para interactuar con el servicio de clientes.
 * Proporciona métodos para verificar la existencia de clientes en el sistema.
 */
@Component
@Slf4j
public class ClientesClient {

    private final RestClient restClient;

    public ClientesClient(
            RestClient.Builder builder,
            @Value("${servicios.clientes.url}") String baseUrl) {
        this.restClient = builder
                .baseUrl(baseUrl)
                .requestFactory(new JdkClientHttpRequestFactory())
                .build();
    }

    /**
     * Verifica si un cliente existe en el servicio de clientes.
     *
     * @param clienteId El ID del cliente a verificar.
     * @return true si el cliente existe (HTTP 2xx), false si no existe (404) o falla la llamada.
     */
    public boolean existeCliente(UUID clienteId) {
        try {
            String traceId = MDC.get("traceId");
            restClient.get()
                    .uri("/clientes/{id}", clienteId)
                    .headers(headers -> {
                        if (traceId != null && !traceId.isBlank()) {
                            headers.set("X-Correlation-ID", traceId);
                        }
                    })
                    .retrieve()
                    .toBodilessEntity();
            return true;
        } catch (RestClientException e) {
            log.warn("No se pudo verificar el cliente {}: {}", clienteId, e.getMessage());
            return false;
        }
    }
}