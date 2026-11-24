package com.tiendaonline.clientes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor


public class ClienteResponse {
    private UUID id;
    private String nombre;
    private String email;
    private LocalDateTime fechaAlta;
}
