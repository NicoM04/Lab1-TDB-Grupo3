package com.example.demo.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notificacion {
    private Long idNotificacion;
    private Long idPedido;
    private LocalDate fechaCreacion;
    private String mensaje;
    private String tipo;
    private Boolean leida;
    private String descripcion;
}
