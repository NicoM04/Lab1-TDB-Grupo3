package com.example.demo.Entity;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Detalle_pedido {
    private Long idDetalle;

    private Long idProducto;
    private Integer cantidad;
    private BigDecimal subtotal;
}
