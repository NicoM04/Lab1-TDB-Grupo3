package com.example.demo.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedioDePago {
    private Integer id_pago;
    private String metodo_pago;
    private LocalDate fecha_pago;
    private Integer monto_total;
}
