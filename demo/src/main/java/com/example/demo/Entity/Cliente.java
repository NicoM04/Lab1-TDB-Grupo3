package com.example.demo.Entity;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cliente {

        private Long idCliente;
        private String nombreCliente;
        private String contrasenaCliente;
        private String correoCliente;
        private String direccion;
        private String telefono;
        private LocalDate fechaRegistro;

}
