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
 */
@Component
@Slf4j
public class ClientesClient {

    private final RestClient restClient;
    /**
     * Constructor para inicializar el cliente REST con la URL base del servicio de clientes.
     *
     * @param builder  El constructor de RestClient.
     * @param baseUrl  La URL base del servicio de clientes, inyectada desde las propiedades de configuración.
     */
    public ClientesClient(
            RestClient.Builder builder,
            @Value("${servicios.clientes.url}") String baseUrl) {
        this.restClient = builder
                .baseUrl(baseUrl)
                .requestFactory(new JdkClientHttpRequestFactory())
                .build();
    }
    /**
     * Verifica si un cliente existe en el sistema.
     *
     * @param clienteId El UUID del cliente a verificar.
     * @return true si el cliente existe, false en caso contrario.
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