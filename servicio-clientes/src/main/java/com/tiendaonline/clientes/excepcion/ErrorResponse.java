package com.tiendaonline.clientes.excepcion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class ErrorResponse {

    private String titulo;
    private int estado;
    private String detalle;
    private String instancia;
    private LocalDateTime fecha;


}
