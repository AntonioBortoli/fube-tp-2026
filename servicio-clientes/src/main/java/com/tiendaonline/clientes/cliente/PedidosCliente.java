package com.tiendaonline.clientes.cliente;

import com.tiendaonline.clientes.dto.PedidoResumenResponse;
import com.tiendaonline.clientes.excepcion.ServicioExternoExcepcion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.slf4j.MDC;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.UUID;


@Component
@Slf4j


public class PedidosCliente {

    private final RestClient restClient;


    public PedidosCliente(
            @Value("${servicios.pedidos.url}") String baseUrl,
            @Value("${servicios.pedidos.connect-timeout-ms:3000}") long connectTimeoutMs,
            @Value("${servicios.pedidos.read-timeout-ms:3000}") long readTimeoutMs)
    {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofMillis(connectTimeoutMs));
        requestFactory.setReadTimeout(Duration.ofMillis(readTimeoutMs));


        this.restClient=RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .build();
    }

    public List<PedidoResumenResponse> obtenerPedidosPorCliente(UUID clienteId){

        try{

            String traceId = MDC.get("traceId");

            List<PedidoResumenResponse> pedidos = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/pedidos")
                            .queryParam("clienteId", clienteId)
                            .build())
                    .headers(headers ->{
                        if(
                                traceId!=null && !traceId.isBlank()
                        ){
                            headers.set("X-Correlation-ID", traceId);
                        }
                    }
                            )
                    .retrieve()
                    .body(new ParameterizedTypeReference <List<PedidoResumenResponse>>() {});

                return pedidos != null? pedidos : Collections.emptyList();
        }catch(RestClientException e){

            log.error("Error al consultar servicio-pedidos para el cliente {}: {}", clienteId, e.getMessage());
            throw new ServicioExternoExcepcion(
                    "No se pudieron obtener los pedidos porque el servicio de pedidos no está disponible.",e
            );

        }
    }

}
