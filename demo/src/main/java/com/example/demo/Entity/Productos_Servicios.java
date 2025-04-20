package com.example.demo.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Productos_Servicios {
    private Long idProducto;
    private String nombreProducto;
    private String descripcion;
    private String categoria;
    private BigDecimal precioUnitario;
    private Integer stock;
}
