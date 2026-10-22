package com.tiendaonline.pedidos;

import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
/**
 * Clase principal de la aplicación ServicioPedidos.
 */
@SpringBootApplication
public class ServicioPedidosApplication {
    /**
     * Método principal que inicia la aplicación.
     * @param args Argumentos de línea de comandos.
     */
    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("America/Argentina/Buenos_Aires"));
        SpringApplication.run(ServicioPedidosApplication.class, args);
    }
}
