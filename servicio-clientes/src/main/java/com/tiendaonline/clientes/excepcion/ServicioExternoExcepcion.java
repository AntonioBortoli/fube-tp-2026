package com.tiendaonline.clientes.excepcion;




public class ServicioExternoExcepcion extends RuntimeException {

    public ServicioExternoExcepcion(String mensaje) {
        super(mensaje);
    }


    public ServicioExternoExcepcion(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
