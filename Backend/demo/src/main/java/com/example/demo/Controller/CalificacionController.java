package com.example.demo.Controller;

import com.example.demo.Entity.Calificacion;
import com.example.demo.Entity.DetalleDePedido;
import com.example.demo.Service.CalificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/calificacion")
public class CalificacionController {

    @Autowired
    private CalificacionService calificacionService;

    @PostMapping("/create")
    public Calificacion crearCalificacion(@RequestBody Calificacion calificacion) {
        return calificacionService.crear(calificacion);
    }

    @GetMapping("/getAll")
    public List<Calificacion> obtenerTodos() {
        return calificacionService.getall();
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<Calificacion> obtenerPorId(@PathVariable("id") Integer id) {
        Calificacion calificacion = calificacionService.findById(id);
        if (calificacion == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Si no se encuentra el calificacion
        }
        return new ResponseEntity<>(calificacion, HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public String actualizarCalificacion(@RequestBody Calificacion calificacion, @PathVariable Integer id) {
        return calificacionService.update(calificacion, id);
    }

    @DeleteMapping("/delete/{id}")
    public void eliminarCalificacion(@PathVariable Integer id) {
        calificacionService.delete(id);
    }
}
