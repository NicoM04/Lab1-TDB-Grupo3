package com.example.demo.Entity;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Repartidor {
    private Long idRepartidor;
    private String nombreRepartidor;
    private String rut;
    private String telefono;
    private LocalDate fechaContratacion;
    private Boolean activo;
    private Integer cantidadEntregas;
    private Integer puntuacion;
}
