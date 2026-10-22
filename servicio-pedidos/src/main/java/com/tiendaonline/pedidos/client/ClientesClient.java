package com.tiendaonline.pedidos.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;
/**
 * Cliente REST para interactuar con el servicio de clientes.
 * Proporciona métodos para verificar la existencia de clientes en el sistema.
 */
@Component
public class ClientesClient {

    private final RestClient restClient;
    /**
     * Constructor para inicializar el cliente REST con la URL base del servicio de clientes.
     *
     * @param baseUrl La URL base del servicio de clientes, inyectada desde las propiedades de configuración.
     */
    public ClientesClient(@Value("${servicios.clientes.url}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
    /**
     * Verifica si un cliente existe en el servicio de clientes.
     *
     * @param clienteId El ID del cliente a verificar.
     * @return true si el cliente existe, false si no existe o si ocurre un error.
     */
    public boolean existeCliente(UUID clienteId) {
        try {
            restClient.get()
                    .uri("/clientes/{id}", clienteId)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                        // Si da 404 u otro 4xx, el cliente no existe
                    })
                    .toBodilessEntity();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}