package com.tiendaonline.inventario.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tiendaonline.inventario.dto.ReservaRequest;
import com.tiendaonline.inventario.model.Producto;
import com.tiendaonline.inventario.repository.ProductoRepository;
import com.tiendaonline.inventario.repository.ReservaRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class InventarioIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    private Producto productoGuardado;

    @BeforeEach
    void setUp() {

        reservaRepository.deleteAll();
        productoRepository.deleteAll();

        Producto producto = new Producto();
        producto.setSku("SKU-INT-001");
        producto.setNombre("Producto Integración");
        producto.setCantidadDisponible(100);
        productoGuardado = productoRepository.save(producto);
   
   
    }

    @Test
    void consultarDisponibilidad_productoExiste_debeRetornar200() throws Exception {
       
       
        mockMvc.perform(get("/api/productos/{id}/disponibilidad", productoGuardado.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productoId").value(productoGuardado.getId().toString()))
                .andExpect(jsonPath("$.sku").value("SKU-INT-001"))
                .andExpect(jsonPath("$.nombre").value("Producto Integración"))
                .andExpect(jsonPath("$.cantidadDisponible").value(100));
    }

    @Test
    void consultarDisponibilidad_productoNoExiste_debeRetornar404() throws Exception {
       
       
        UUID idInexistente = UUID.randomUUID();

        mockMvc.perform(get("/api/productos/{id}/disponibilidad", idInexistente))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void reservarStock_conStockSuficiente_debeRetornar201() throws Exception {
       
       
        ReservaRequest request = new ReservaRequest(
                productoGuardado.getId(), UUID.randomUUID(), 30);



        mockMvc.perform(post("/api/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cantidad").value(30))
                .andExpect(jsonPath("$.estado").value("ACTIVA"));

        Producto actualizado = productoRepository.findById(productoGuardado.getId()).orElseThrow();
        assertEquals(70, actualizado.getCantidadDisponible());
    }




    @Test
    void reservarStock_sinStockSuficiente_debeRetornar409() throws Exception {
        ReservaRequest request = new ReservaRequest(
                productoGuardado.getId(), UUID.randomUUID(), 999);


        mockMvc.perform(post("/api/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").exists());
   
   
   
     }



    @Test
    void reservarStock_datosInvalidos_debeRetornar400() throws Exception {
        
        String jsonInvalido = """
                {
                    "productoId": null,
                    "pedidoId": null,
                    "cantidad": null
                }
                """;


        mockMvc.perform(post("/api/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInvalido))
                .andExpect(status().isBadRequest());
    

    }




    @Test
    void liberarReserva_flujoCompleto_debeVolverStock() throws Exception {
       
       
        ReservaRequest request = new ReservaRequest(
                productoGuardado.getId(), UUID.randomUUID(), 40);



        MvcResult resultado = mockMvc.perform(post("/api/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();




        String responseJson = resultado.getResponse().getContentAsString();
        String reservaId = objectMapper.readTree(responseJson).get("id").asText();


        Producto despuesDeReservar = productoRepository.findById(productoGuardado.getId()).orElseThrow();
        assertEquals(60, despuesDeReservar.getCantidadDisponible());



        mockMvc.perform(patch("/api/reservas/{id}/liberar", reservaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("LIBERADA"));

        Producto despuesDeLiberar = productoRepository.findById(productoGuardado.getId()).orElseThrow();
        assertEquals(100, despuesDeLiberar.getCantidadDisponible());
    
    
    
    }



}
