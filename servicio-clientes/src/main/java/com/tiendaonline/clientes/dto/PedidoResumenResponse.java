package com.tiendaonline.clientes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class PedidoResumenResponse {

    private UUID id;
    private String estado;
    private BigDecimal montoTotal;
    private LocalDate fechaCreacion;


}
