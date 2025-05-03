package com.example.demo.Repository;

import com.example.demo.Entity.Calificacion;
import org.sql2o.Sql2o;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CalificacionRepositoryImp implements CalificacionRepository {

    @Autowired
    private Sql2o sql2o;

    @Override
    public Calificacion crear(Calificacion calificacion) {
        String sql = "INSERT INTO calificacion (id_repartidor, comentario, fecha_calificacion) " +
                "VALUES (:id_repartidor, :comentario, :fecha_calificacion)";
        try (var con = sql2o.open()) {
            int id = (int) con.createQuery(sql, true)
                    .addParameter("id_repartidor", calificacion.getId_repartidor())
                    .addParameter("comentario", calificacion.getComentario())
                    .addParameter("fecha_calificacion", calificacion.getFecha_calificacion())
                    .executeUpdate()
                    .getKey();
            calificacion.setId_calificacion(id);
            return calificacion;
        }
    }

    @Override
    public List<Calificacion> getAll() {
        String sql = "SELECT id_calificacion, id_repartidor, valor AS puntuacion, comentario, fecha_calificacion FROM calificacion";
        try (var con = sql2o.open()) {
            return con.createQuery(sql)
                    .executeAndFetch(Calificacion.class);
        }
    }

    @Override
    public String update(Calificacion calificacion, Integer id) {
        String sql = "UPDATE calificacion SET id_repartidor = :id_repartidor, " +
                "comentario = :comentario, fecha_calificacion = :fecha_calificacion " +
                "WHERE id_calificacion = :id_calificacion";
        try (var con = sql2o.open()) {
            int result = con.createQuery(sql)
                    .addParameter("id_repartidor", calificacion.getId_repartidor())
                    .addParameter("comentario", calificacion.getComentario())
                    .addParameter("fecha_calificacion", calificacion.getFecha_calificacion())
                    .addParameter("id_calificacion", id)
                    .executeUpdate()
                    .getResult();

            return result > 0 ? "Calificación actualizada exitosamente" : "No se encontró la calificación";
        }
    }

    @Override
    public void delete(Integer id) {
        String sql = "DELETE FROM calificacion WHERE id_calificacion = :id_calificacion";
        try (var con = sql2o.open()) {
            con.createQuery(sql)
                    .addParameter("id_calificacion", id)
                    .executeUpdate();
        }
    }

    @Override
    public Calificacion findById(Integer id) {
        String sql = "SELECT * FROM calificacion WHERE id_calificacion = :id_calificacion";
        try (var con = sql2o.open()) {
            return con.createQuery(sql)
                    .addParameter("id_calificacion", id)
                    .executeAndFetchFirst(Calificacion.class);
        }
    }
}