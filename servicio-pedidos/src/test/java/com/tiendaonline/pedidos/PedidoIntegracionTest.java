package com.tiendaonline.pedidos;

import com.tiendaonline.pedidos.dto.request.PedidoRequest;
import com.tiendaonline.pedidos.dto.request.ItemPedidoRequest;
import com.tiendaonline.pedidos.dto.response.PedidoResponse;
import com.tiendaonline.pedidos.model.enums.EstadoPedido;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PedidoIntegracionTest {

    private static ClientAndServer mockServer;

    @Autowired
    private TestRestTemplate restTemplate;

    // Levantamos MockServer en un puerto libre antes de arrancar los tests
    @BeforeAll
    static void iniciarMockServer() {
        mockServer = startClientAndServer(1080);
    }

    // Le decimos a Spring Boot que apunte los clients HTTP al puerto de MockServer
    @DynamicPropertySource
    static void configurarUrls(DynamicPropertyRegistry registry) {
        registry.add("servicios.clientes.url", () -> "http://localhost:1080");
        registry.add("servicios.inventario.url", () -> "http://localhost:1080");
    }

    @AfterAll
    static void detenerMockServer() {
        if (mockServer != null) {
            mockServer.stop();
        }
    }

    @Test
    void crearPedido_flujoCompleto_conServiciosSimuladosPorMockServer() {
        UUID clienteId = UUID.randomUUID();
        UUID productoId = UUID.randomUUID();

        // 1. Simulamos que servicio-clientes (8081) responde 200 OK
        mockServer.when(
                HttpRequest.request()
                        .withMethod("GET")
                        .withPath("/clientes/" + clienteId)
        ).respond(
                HttpResponse.response()
                        .withStatusCode(200)
        );

        // 2. Simulamos que servicio-inventario (8082) responde 200 OK al reservar stock
        mockServer.when(
                HttpRequest.request()
                        .withMethod("POST")
                        .withPath("/reservas")
        ).respond(
                HttpResponse.response()
                        .withStatusCode(200)
        );

        // 3. Enviamos un POST real a nuestro propio controller de pedidos
        PedidoRequest request = PedidoRequest.builder()
                .clienteId(clienteId)
                .items(List.of(
                        ItemPedidoRequest.builder()
                                .productoId(productoId)
                                .cantidad(3)
                                .precioUnitario(new BigDecimal("150.00"))
                                .build()
                ))
                .build();

        ResponseEntity<PedidoResponse> response = restTemplate.postForEntity(
                "/pedidos", request, PedidoResponse.class);

        // 4. Verificaciones
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(clienteId, response.getBody().getClienteId());
        assertEquals(EstadoPedido.CREADO, response.getBody().getEstado());
        // 3 unidades * $150.00 = $450.00
        assertEquals(new BigDecimal("450.00"), response.getBody().getMontoTotal());
    }
}