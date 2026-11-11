package com.tiendaonline.clientes.servicio;


import com.tiendaonline.clientes.cliente.PedidosCliente;
import com.tiendaonline.clientes.dto.ClienteConPedidosResponse;
import com.tiendaonline.clientes.dto.ClienteResponse;
import com.tiendaonline.clientes.dto.CrearClienteRequest;
import com.tiendaonline.clientes.dto.PedidoResumenResponse;
import com.tiendaonline.clientes.excepcion.EmailDuplicadoException;
import com.tiendaonline.clientes.excepcion.RecursoNoEncontradoException;
import com.tiendaonline.clientes.modelo.Cliente;
import com.tiendaonline.clientes.repositorio.ClienteRepositorio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;



import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;




@ExtendWith(MockitoExtension.class)

class ClienteServicioImpTest {

    @Mock
    private ClienteRepositorio repositorio;


    @Mock
    private PiiMaskingServicio piiMaskingServicio;

    @Mock
    private PedidosCliente pedidosCliente;


    @InjectMocks
    private ClienteServicioImp servicio;


    @Test
    @DisplayName("Debe registrar un cliente cuando el email no está duplicado")
    void registrarClienteConEmailNuevoGuardaYRetornaResponse() {
        CrearClienteRequest request = CrearClienteRequest.builder()
                .nombre("Pancho Soltermann")
                .email("pancho.soltermann@frsf.utn.edu.ar")
                .build();

        UUID idGenerado = UUID.randomUUID();
        Cliente clienteGuardado = Cliente.builder()
                .id(idGenerado)
                .nombre("Pancho Soltermann")
                .email("pancho.soltermann@frsf.utn.edu.ar")
                .fechaAlta(LocalDateTime.now())
                .build();

        when(repositorio.existsByEmail("pancho.soltermann@frsf.utn.edu.ar")).thenReturn(false);
        when(repositorio.save(any(Cliente.class))).thenReturn(clienteGuardado);


        ClienteResponse respuesta = servicio.registrarCliente(request);

        assertNotNull(respuesta);
        assertEquals(idGenerado, respuesta.getId());
        assertEquals("Pancho Soltermann", respuesta.getNombre());
        verify(repositorio).save(any(Cliente.class));

    }


    @Test
    @DisplayName("Debe lanzar EmailDuplicadoException cuando el email ya existe")
    void registrarClienteConEmailDuplicadoLanzaExcepcion() {
        CrearClienteRequest request = CrearClienteRequest.builder()
                .nombre("Pancho Soltermann")
                .email("pancho.soltermann@frsf.utn.edu.ar")
                .build();

        when(repositorio.existsByEmail("pancho.soltermann@frsf.utn.edu.ar")).thenReturn(true);

        assertThrows(EmailDuplicadoException.class, () ->
                servicio.registrarCliente(request));
        verify(repositorio, never()).save(any(Cliente.class));
    }


    @Test
    @DisplayName("Debe lanzar RecursoNoEncontradoException si el ID no existe")
    void obtenerClientePorIdInexistenteLanzaExcepcion() {
        UUID id = UUID.randomUUID();

        when(repositorio.findById(id)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () ->
                servicio.obtenerClientePorId(id));

    }


    @Test
    @DisplayName("Debe enmascarar nombre y email cuando el feature flag mask-pii esta activo")
    void obtenerClientePorIdConFlagActivoRestornaDatosEnmascarados() {
        UUID id = UUID.randomUUID();
        Cliente cliente = Cliente.builder()
                .id(id)
                .nombre("Pancho Soltermann")
                .email("pancho.soltermann@frsf.utn.edu.ar")
                .fechaAlta(LocalDateTime.now())
                .build();


        when(repositorio.findById(id)).thenReturn(Optional.of(cliente));
        when(piiMaskingServicio.debeEnmascarar(id.toString())).thenReturn(true);
        when(piiMaskingServicio.enmascararNombre("Pancho Soltermann")).thenReturn("P***** S*********");
        when(piiMaskingServicio.enmascararEmail("pancho.soltermann@frsf.utn.edu.ar")).thenReturn("***@***.ar");


        ClienteResponse respuesta = servicio.obtenerClientePorId(id);


        assertEquals("P***** S*********", respuesta.getNombre());
        assertEquals("***@***.ar", respuesta.getEmail());
    }



    @Test
    @DisplayName("Debe retornar el cliente junto con sus pedidos obtenidos del servicio externo")
    void obtenerClienteConPedidosClienteExistenteRetornaClienteYPedidos() {
        UUID id = UUID.randomUUID();
        Cliente cliente = Cliente.builder()
                .id(id)
                .nombre("Pancho Soltermann")
                .email("pancho.soltermann@frsf.utn.edu.ar")
                .fechaAlta(LocalDateTime.now())
                .build();

        PedidoResumenResponse pedido = PedidoResumenResponse.builder()
                .id(UUID.randomUUID())
                .estado("CONFIRMADO")
                .montoTotal(new BigDecimal("15000.00"))
                .fechaCreacion(LocalDateTime.now())
                .build();

        when(repositorio.findById(id)).thenReturn(Optional.of(cliente));
        when(piiMaskingServicio.debeEnmascarar(id.toString())).thenReturn(false);
        when(pedidosCliente.obtenerPedidosPorCliente(id)).thenReturn(List.of(pedido));


        ClienteConPedidosResponse resultado = servicio.obtenerClienteConPedidos(id);

        assertNotNull(resultado);
        assertEquals(id, resultado.getCliente().getId());
        assertEquals(1, resultado.getPedidos().size());
        assertEquals("CONFIRMADO", resultado.getPedidos().get(0).getEstado());


    }
    }
