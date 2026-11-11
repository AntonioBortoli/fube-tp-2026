package com.tiendaonline.clientes.controlador;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.tiendaonline.clientes.dto.CrearClienteRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;



import java.util.UUID;



import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(properties = "statsig.secret-key=")


public class ClienteControladorTest {
    @Autowired
    private MockMvc mockMvc;



    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @DisplayName("POST /clientes valido debe retornar 201 Created y cabecera Location")
    void registrarClienteDatosValidosRetorna201() throws Exception {

        CrearClienteRequest pedido= CrearClienteRequest.builder()
                .nombre("Nico Melgratti")
                .email("nmelgratti@frsf.utn.edu.ar")
                .build();



        mockMvc.perform(post("/clientes")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(pedido)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nombre").value("Nico Melgratti"))
                .andExpect(jsonPath("$.email").value("nmelgratti@frsf.utn.edu.ar"));

    }


    @Test
    @DisplayName("GET /clientes/{id} inexistente debe retornar 404 Not Found")
    void obtenerClientePorIdInexistenteRetorna404() throws Exception {

        UUID idInexistente= UUID.randomUUID();


        mockMvc.perform(get("/clientes/{id}",idInexistente))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.titulo").value("Recurso no encontrado"))
                .andExpect(jsonPath("$.estado").value(404));
    }

    @Test
    @DisplayName("POST /clientes con email invalido debe retornar 400 Bad Request")
    void registrarClienteEmailInvalidoRetorna400() throws Exception {

        CrearClienteRequest pedido = CrearClienteRequest.builder()
                .nombre("Nico Melgratti")
                .email("email-invalido")
                .build();


        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pedido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.titulo").value("Validación fallida"))
                .andExpect(jsonPath("$.estado").value(400));
    }



    @Test
    @DisplayName("POST /clientes con email repetido debe retornar 409 Conflict")
    void registrarClienteEmailDuplicadoRetorna409() throws Exception {

        CrearClienteRequest pedido = CrearClienteRequest.builder()
                .nombre("Nico Melgratti")
                .email("nmelgratti.duplicado@frsf.utn.edu.ar")
                .build();



        String json = objectMapper.writeValueAsString(pedido);

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());


        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.titulo").value("Email duplicado"))
                .andExpect(jsonPath("$.estado").value(409));
    }


}
