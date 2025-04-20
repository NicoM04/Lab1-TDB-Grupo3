package com.example.demo.Entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Empresa_asociada {
    private Long idEmpresa;
    private String nombreEmpresa;
    private String rutEmpresa;
    private String correoContacto;
    private String direccion;
}
