package com.tiendaonline.clientes.servicio;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;


class PiiMaskingServicioTest {
    private PiiMaskingServicio piiMaskingServicio;


    @BeforeEach
    void setUp() {
        piiMaskingServicio= new PiiMaskingServicio();
    }

    @Test
    @DisplayName("Debe enmascarar un nombre compuesto conservando solo las iniciales")
    void enmascararNombreCompuestoRetornaInicialesConAsteriscos(){
        String resultado= piiMaskingServicio.enmascararNombre("Pancho Soltermann");
        assertEquals("P*** S***",resultado);
    }


    @Test
    @DisplayName("Debe devolver nulo o vacio si el nombre es nulo o en blanco")
    void enmascararNombreNuloOVacioRetornaNuloOVacio(){
        assertNull(piiMaskingServicio.enmascararNombre(null));
        assertEquals(" ", piiMaskingServicio.enmascararNombre(" "));
    }


    @Test
    @DisplayName("Debe enmascarar email conservando la extensión del dominio")
    void enmascararEmailValidoConservaExtension(){
        assertEquals("***@***.com", piiMaskingServicio.enmascararEmail("pancho@gmail.com"));
        assertEquals("***@***.ar", piiMaskingServicio.enmascararEmail("alumno@frsf.utn.edu.ar"));
    }


    @Test
    @DisplayName("Si el SDK de Statsig no fue inicializado, debeEnmascarar retorna false de forma segura")
    void debeEnmascararSinSdkInicializadoRetornaFalse(){
        assertFalse(piiMaskingServicio.debeEnmascarar("cualquier-id"));
    }


}
