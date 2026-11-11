package com.tiendaonline.clientes.cliente;


import com.tiendaonline.clientes.dto.PedidoResumenResponse;
import com.tiendaonline.clientes.excepcion.ServicioExternoExcepcion;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.MediaType;
import org.slf4j.MDC;



import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.HttpRequest.request;



public class PedidosClienteIntegracionTest {

    private ClientAndServer mockServer;
    private PedidosCliente pedidosCliente;

    @BeforeEach
    void iniciarMockServer() {

        mockServer = ClientAndServer.startClientAndServer(0);
        String baseUrl = "http://localhost:" + mockServer.getLocalPort();
        pedidosCliente = new PedidosCliente(baseUrl, 500, 500);
    }


    @AfterEach
    void detenerMockServer() {
        MDC.clear();
        if (mockServer != null) {
            mockServer.stop();
        }

    }


    @Test
    @DisplayName("Debe obetner la lista de pedidos y propagar el header X-Correlation-ID cuando servicio-pedidos responde 200")
    void obtenerPedidosPorClienteRespuesta200RetornaListaYPropagaTraceId() {
        UUID clienteId = UUID.randomUUID();
        UUID pedidoId = UUID.randomUUID();
        MDC.put("traceId", "test-trace-123");

        String jsonRespuesta = """
                [
                    {
                        "id": "%s",
                        "estado": "CONFIRMADO",
                        "fecha": "2026-11-09T15:30:00",
                        "total": 25000.50
                    }
                ]
                
                """.formatted(pedidoId);


        mockServer.when(
                request()
                        .withMethod("GET")
                        .withPath("/pedidos")
                        .withQueryStringParameter("clienteId", clienteId.toString())
        ).respond(
                response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(jsonRespuesta)
        );


        List<PedidoResumenResponse> pedidos = pedidosCliente.obtenerPedidosPorCliente(pedidoId);


        assertEquals(1, pedidos.size());
        assertEquals(pedidoId, pedidos.getFirst().getId());
        assertEquals("CONFIRMADO", pedidos.getFirst().getEstado());
        assertEquals(new BigDecimal("25000.50"), pedidos.getFirst().getMontoTotal());

        mockServer.verify(
                request()
                        .withPath("/pedidos")
                        .withHeader("X-Correlation-ID", "test-trace-123")
        );


    }


    @Test
    @DisplayName("Debe lanzar ServicioExternoExcepcion cuando servicio-pedidos responde 500")
    void obtenerPedidosPorClienteError500LanzaServicioExternoExcepcion() {

        UUID clienteId = UUID.randomUUID();

        mockServer.when(
                request()
                        .withMethod("GET")
                        .withPath("/pedidos")
                        .withQueryStringParameter("clienteId", clienteId.toString())
        ).respond(
                response()
                        .withStatusCode(500)
        );

        assertThrows(ServicioExternoExcepcion.class, () -> pedidosCliente.obtenerPedidosPorCliente(clienteId));
    }


    @Test
    @DisplayName("Debe lanar ServicioExternoExcepcion cuando servicio-pedidos supera el read-timeout")
    void obtenerPedidosPorClienteTimeoutLanzaServicioExternoExcepcion() {

        UUID clienteId = UUID.randomUUID();

        mockServer.when(
                request()
                        .withMethod("GET")
                        .withPath("/pedidos")
                        .withQueryStringParameter("clienteId", clienteId.toString())
        ).respond(
                response()
                        .withStatusCode(200)
                        .withDelay(TimeUnit.SECONDS, 1500)
        );


        assertThrows(ServicioExternoExcepcion.class, () -> pedidosCliente.obtenerPedidosPorCliente(clienteId));
    }


}

