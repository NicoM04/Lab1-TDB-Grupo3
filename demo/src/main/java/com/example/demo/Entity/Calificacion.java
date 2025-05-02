package com.example.demo.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Calificacion {
    private Integer id_calificacion;
    private Integer id_repartidor;
    private Integer puntuacion;
    private String comentario;
    private LocalDate fecha_calificacion;
}
