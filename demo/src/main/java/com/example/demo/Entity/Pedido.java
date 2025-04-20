package com.example.demo.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pedido {
    private Long idPedido;
    private Long idCliente;
    private Long idEmpresa;
    private Long idRepartidor;
    private Long idDetalle;
    private LocalDate fechaPedido;
    private LocalDate fechaEntrega;
    private String estado;
    private Boolean urgente;
}
