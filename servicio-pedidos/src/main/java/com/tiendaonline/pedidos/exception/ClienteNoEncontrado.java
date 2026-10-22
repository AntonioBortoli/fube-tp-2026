package com.tiendaonline.pedidos.exception;

import java.util.UUID;
/**
 * Excepción personalizada que se lanza cuando un cliente no es encontrado en la base de datos.
 */

public class ClienteNoEncontrado extends RuntimeException  {
    /**
     * Constructor que recibe el UUID del cliente no encontrado y construye un mensaje de error.
     * @param clienteId El UUID del cliente que no fue encontrado.
     */
    public ClienteNoEncontrado(UUID clienteId) {
        super("El cliente con id " + clienteId + " no existe.");
    }
}
