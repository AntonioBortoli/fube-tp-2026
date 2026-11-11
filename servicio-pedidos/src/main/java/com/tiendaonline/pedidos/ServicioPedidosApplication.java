package com.tiendaonline.pedidos;

import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
/**
 * Clase principal de la aplicación ServicioPedidos.
 * Configura la zona horaria predeterminada y arranca la aplicación Spring Boot.
 */
@SpringBootApplication
@EnableScheduling
public class ServicioPedidosApplication {
    /**
     * Método principal que arranca la aplicación Spring Boot.
     * Configura la zona horaria predeterminada a "America/Argentina/Buenos_Aires".
     *
     * @param args Argumentos de línea de comandos (no utilizados).
     */
    public static void main(String[] args) {
        /**
         * Configura la zona horaria predeterminada de la aplicación.
         * Esto asegura que todas las operaciones de fecha y hora se realicen en la zona horaria especificada.
         */
        TimeZone.setDefault(TimeZone.getTimeZone("America/Argentina/Buenos_Aires"));
        /** Arranca la aplicación Spring Boot.
         * SpringApplication.run() inicia el contexto de la aplicación y carga todos los beans necesarios.
         */
        SpringApplication.run(ServicioPedidosApplication.class, args);
    }
}
