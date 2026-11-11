package com.tiendaonline.pedidos;

import com.tiendaonline.pedidos.dto.request.PedidoRequest;
import com.tiendaonline.pedidos.dto.request.ItemPedidoRequest;
import com.tiendaonline.pedidos.dto.response.PedidoResponse;
import com.tiendaonline.pedidos.model.enums.EstadoPedido;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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
/**
 * Clase de prueba de integración para el servicio de pedidos.
 * Utiliza MockServer para simular las respuestas de los servicios externos (Clientes e Inventario).
 * Contiene pruebas para la creación y cancelación de pedidos, verificando la interacción con los servicios simulados.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PedidoIntegracionTest {

    private static ClientAndServer mockServer;

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeAll
    static void iniciarMockServer() {
        mockServer = startClientAndServer(1080);
    }
    /**
     * Limpia las expectativas del MockServer antes de cada prueba.
     * Esto asegura que cada prueba comience con un estado limpio del servidor simulado.
     */
    @BeforeEach
    void limpiarExpectations() {
        if (mockServer != null) {
            mockServer.reset();
        }
    }
    /**
     * Configura las propiedades dinámicas para los servicios externos.
     * Establece las URLs de los servicios de Clientes e Inventario para que apunten al MockServer.
     *
     * @param registry Registro de propiedades dinámicas.
     */
    @DynamicPropertySource
    static void configurarUrls(DynamicPropertyRegistry registry) {
        registry.add("servicios.clientes.url", () -> "http://localhost:1080");
        registry.add("servicios.inventario.url", () -> "http://localhost:1080");
    }
    /**
     * Detiene el MockServer después de todas las pruebas.
     * Esto libera los recursos utilizados por el servidor simulado.
     */
    @AfterAll
    static void detenerMockServer() {
        if (mockServer != null) {
            mockServer.stop();
        }
    }
    /**
     * Prueba de integración para la creación de un pedido.
     * Simula la existencia del cliente y la disponibilidad de stock, y verifica que el pedido se cree correctamente.
     */
    @Test
    void crearPedido_flujoCompleto_conServiciosSimuladosPorMockServer() {
        UUID clienteId = UUID.randomUUID();
        UUID productoId = UUID.randomUUID();

        mockServer.when(
                HttpRequest.request()
                        .withMethod("GET")
                        .withPath("/clientes/" + clienteId)
        ).respond(
                HttpResponse.response()
                        .withStatusCode(200)
        );

        mockServer.when(
                HttpRequest.request()
                        .withMethod("POST")
                        .withPath("/api/reservas")
        ).respond(
                HttpResponse.response()
                        .withStatusCode(201)
        );

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

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(clienteId, response.getBody().getClienteId());
        assertEquals(EstadoPedido.CREADO, response.getBody().getEstado());
        assertEquals(new BigDecimal("450.00"), response.getBody().getMontoTotal());
    }
    /**
     * Prueba de integración para la creación de un pedido cuando el cliente no existe.
     * Simula una respuesta 404 del servicio de clientes y verifica que se retorne un estado 404 al intentar crear el pedido.
     */
    @Test
    void crearPedido_cuandoClienteNoExiste_devuelve404() {
        UUID clienteId = UUID.randomUUID();
        UUID productoId = UUID.randomUUID();

        mockServer.when(
                HttpRequest.request()
                        .withMethod("GET")
                        .withPath("/clientes/" + clienteId)
        ).respond(
                HttpResponse.response().withStatusCode(404)
        );

        PedidoRequest request = PedidoRequest.builder()
                .clienteId(clienteId)
                .items(List.of(
                        ItemPedidoRequest.builder()
                                .productoId(productoId)
                                .cantidad(1)
                                .precioUnitario(new BigDecimal("100.00"))
                                .build()
                ))
                .build();

        ResponseEntity<String> response = restTemplate.postForEntity("/pedidos", request, String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
    /**
     * Prueba de integración para la creación de un pedido cuando no hay suficiente stock.
     * Simula una respuesta 409 del servicio de inventario y verifica que se retorne un estado 409 al intentar crear el pedido.
     */
    @Test
    void crearPedido_cuandoNoHayStock_devuelve409() {
        UUID clienteId = UUID.randomUUID();
        UUID productoId = UUID.randomUUID();

        mockServer.when(
                HttpRequest.request()
                        .withMethod("GET")
                        .withPath("/clientes/" + clienteId)
        ).respond(
                HttpResponse.response().withStatusCode(200)
        );

        mockServer.when(
                HttpRequest.request()
                        .withMethod("POST")
                        .withPath("/api/reservas")
        ).respond(
                HttpResponse.response().withStatusCode(409)
        );

        PedidoRequest request = PedidoRequest.builder()
                .clienteId(clienteId)
                .items(List.of(
                        ItemPedidoRequest.builder()
                                .productoId(productoId)
                                .cantidad(10)
                                .precioUnitario(new BigDecimal("100.00"))
                                .build()
                ))
                .build();

        ResponseEntity<String> response = restTemplate.postForEntity("/pedidos", request, String.class);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }
    /**
     * Prueba de integración para la cancelación de un pedido.
     * Simula la existencia del pedido y la liberación de stock, y verifica que el pedido se cancele correctamente.
     */
    @Test
    void cancelarPedido_flujoCompleto_liberaStockEnInventario() {
        UUID clienteId = UUID.randomUUID();
        UUID productoId = UUID.randomUUID();

        mockServer.when(
                HttpRequest.request().withMethod("GET").withPath("/clientes/" + clienteId)
        ).respond(HttpResponse.response().withStatusCode(200));

        mockServer.when(
                HttpRequest.request().withMethod("POST").withPath("/api/reservas")
        ).respond(HttpResponse.response().withStatusCode(201));

        PedidoRequest request = PedidoRequest.builder()
                .clienteId(clienteId)
                .items(List.of(
                        ItemPedidoRequest.builder()
                                .productoId(productoId)
                                .cantidad(2)
                                .precioUnitario(new BigDecimal("50.00"))
                                .build()
                ))
                .build();

        ResponseEntity<PedidoResponse> creado = restTemplate.postForEntity("/pedidos", request, PedidoResponse.class);
        assertEquals(HttpStatus.CREATED, creado.getStatusCode());
        UUID pedidoId = creado.getBody().getId();

        mockServer.when(
                HttpRequest.request()
                        .withMethod("PATCH")
                        .withPath("/api/reservas/pedido/" + pedidoId + "/liberar")
        ).respond(HttpResponse.response().withStatusCode(200));

        ResponseEntity<PedidoResponse> cancelado = restTemplate.postForEntity(
                "/pedidos/" + pedidoId + "/cancelar", null, PedidoResponse.class);

        assertEquals(HttpStatus.OK, cancelado.getStatusCode());
        assertNotNull(cancelado.getBody());
        assertEquals(EstadoPedido.CANCELADO, cancelado.getBody().getEstado());

        mockServer.verify(
                HttpRequest.request()
                        .withMethod("POST")
                        .withPath("/api/reservas")
                        .withBody(org.mockserver.model.JsonBody.json("{\"pedidoId\":\"" + pedidoId + "\"}")),
                org.mockserver.verify.VerificationTimes.once()
        );
    }
}
