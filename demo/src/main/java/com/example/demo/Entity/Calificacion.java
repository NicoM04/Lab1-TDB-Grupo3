package com.example.demo.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Calificacion {
    private Long idCalificacion;
    private Long idRepartidor;
    private Integer puntuacion;
    private String comentario;
    private LocalDate fechaCalificacion;
}
