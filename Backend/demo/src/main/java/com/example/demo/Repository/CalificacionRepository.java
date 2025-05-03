package com.example.demo.Repository;
import com.example.demo.Entity.Calificacion;

import java.util.List;

public interface CalificacionRepository {
    public Calificacion crear(Calificacion calificacion);
    public List<Calificacion> getAll();
    public String update(Calificacion calificacion, Integer id);
    public void delete(Integer id);
    public Calificacion findById(Integer id);
}
