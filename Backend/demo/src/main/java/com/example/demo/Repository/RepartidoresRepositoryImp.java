package com.example.demo.Repository;

import com.example.demo.Entity.Repartidor;
//import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class RepartidoresRepositoryImp implements RepartidoresRepository {

    @Autowired
    private Sql2o sql2o;

    @Override
    public Repartidor crear(Repartidor repartidor) {
        String sql = "INSERT INTO repartidor (nombre_repartidor, rut, telefono, fecha_contratacion, activo, cantidad_entregas) " +
                "VALUES (:nombre_repartidor, :rut, :telefono, :fecha_contratacion, :activo, :cantidad_entregas)";
        try (var con = sql2o.open()) {
            int id = (int) con.createQuery(sql, true)
                    .addParameter("nombre_repartidor", repartidor.getNombre_repartidor())
                    .addParameter("rut", repartidor.getRut())
                    .addParameter("telefono", repartidor.getTelefono())
                    .addParameter("fecha_contratacion", repartidor.getFecha_contratacion())
                    .addParameter("activo", repartidor.getActivo())
                    .addParameter("cantidad_entregas", repartidor.getCantidad_entregas())
                    .executeUpdate()
                    .getKey();
            repartidor.setId_repartidor(id);
            return repartidor;
        }
    }

    @Override
    public List<Repartidor> getAll() {
        String sql = "SELECT * FROM repartidor";
        try (var con = sql2o.open()) {
            return con.createQuery(sql)
                    .executeAndFetch(Repartidor.class);
        }
    }

    @Override
    public Repartidor findById(Integer id) {
        String sql = "SELECT * FROM repartidor WHERE id_repartidor = :id_repartidor";
        try (var con = sql2o.open()) {
            return con.createQuery(sql)
                    .addParameter("id_repartidor", id)
                    .executeAndFetchFirst(Repartidor.class);
        }
    }

    @Override
    public String update(Repartidor repartidor, Integer id) {
        String sql = "UPDATE repartidor SET nombre_repartidor = :nombre_repartidor, rut = :rut, telefono = :telefono, " +
                "fecha_contratacion = :fecha_contratacion, activo = :activo, cantidad_entregas = :cantidad_entregas " +
                "WHERE id_repartidor = :id_repartidor";
        try (var con = sql2o.open()) {
            int result = con.createQuery(sql)
                    .addParameter("nombre_repartidor", repartidor.getNombre_repartidor())
                    .addParameter("rut", repartidor.getRut())
                    .addParameter("telefono", repartidor.getTelefono())
                    .addParameter("fecha_contratacion", repartidor.getFecha_contratacion())
                    .addParameter("activo", repartidor.getActivo())
                    .addParameter("cantidad_entregas", repartidor.getCantidad_entregas())
                    .addParameter("id_repartidor", id)
                    .executeUpdate()
                    .getResult();
            return result > 0 ? "Repartidor actualizado correctamente" : "No se encontró el repartidor";
        }
    }

    @Override
    public void delete(Integer id) {
        String sql = "DELETE FROM repartidor WHERE id_repartidor = :id_repartidor";
        try (var con = sql2o.open()) {
            con.createQuery(sql)
                    .addParameter("id_repartidor", id)
                    .executeUpdate();
        }
    }

    @Override
    public List<Map<String, Object>> obtenerTiempoPromedioEntregaPorRepartidor() {
        try (var conn = sql2o.open()) {
            String sql = """
            SELECT id_repartidor,
                   AVG( fecha_entrega - fecha_pedido) AS tiempo_promedio_dias
            FROM pedido
            WHERE fecha_entrega IS NOT NULL AND fecha_pedido IS NOT NULL
            GROUP BY id_repartidor
        """;

            return conn.createQuery(sql)
                    .executeAndFetchTable()
                    .asList();
        } catch (Exception e) {
            System.out.println("Error al calcular tiempo promedio de entrega: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<Map<String, Object>> obtenerTop3RepartidoresConMejorRendimiento() {
        String sql = """
        SELECT r.id_repartidor,
               r.nombre_repartidor,
               r.cantidad_entregas,
               COALESCE(AVG(c.valor), 0) AS promedio_calificacion,
               -- Métrica compuesta para ordenamiento
               (r.cantidad_entregas * 0.5 + COALESCE(AVG(c.valor), 0) * 10) AS puntaje_rendimiento
        FROM repartidor r
        LEFT JOIN calificacion c ON r.id_repartidor = c.id_repartidor
        GROUP BY r.id_repartidor, r.nombre_repartidor, r.cantidad_entregas
        ORDER BY puntaje_rendimiento DESC
        LIMIT 3
    """;

        try (var conn = sql2o.open()) {
            return conn.createQuery(sql)
                    .executeAndFetchTable()
                    .asList();
        } catch (Exception e) {
            System.out.println("Error al obtener los mejores repartidores: " + e.getMessage());
            return null;
        }
    }
}
