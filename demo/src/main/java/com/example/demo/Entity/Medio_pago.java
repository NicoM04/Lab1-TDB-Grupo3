package com.example.demo.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Medio_pago {
    private Long idPago;
    private Long idDetalle;
    private String metodoPago;
    private LocalDate fechaPago;
    private Integer montoTotal;
}
