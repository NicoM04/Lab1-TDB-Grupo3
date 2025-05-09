package com.example.demo.Repository;

import com.example.demo.Entity.Calificacion;
import com.example.demo.Entity.MedioDePago;
//import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MediosDePagoRepositoryImp implements MediosDePagoRepository {

    @Autowired
    private Sql2o sql2o;

    @Override
    public MedioDePago crear(MedioDePago medioDePago) {
        String sql = "INSERT INTO medio_pago (metodo_pago, fecha_pago, monto_total) " +
                "VALUES (:metodo_pago, :fecha_pago, :monto_total)";
        try (var con = sql2o.open()) {
            int id = (int) con.createQuery(sql, true)
                    .addParameter("metodo_pago", medioDePago.getMetodo_pago())
                    .addParameter("fecha_pago", medioDePago.getFecha_pago())
                    .addParameter("monto_total", medioDePago.getMonto_total())
                    .executeUpdate()
                    .getKey();
            medioDePago.setId_pago(id);
            return medioDePago;
        }
    }

    @Override
    public List<MedioDePago> getAll() {
        String sql = "SELECT id_pago, metodo_pago, fecha_pago, monto_total FROM medio_pago";
        try (var con = sql2o.open()) {
            return con.createQuery(sql)
                    .executeAndFetch(MedioDePago.class);
        }
    }

    @Override
    public String update(MedioDePago medioDePago, Integer id) {
        String sql = "UPDATE medio_pago SET metodo_pago = :metodo_pago, fecha_pago = :fecha_pago, " +
                "monto_total = :monto_total WHERE id_pago = :id_pago";
        try (var con = sql2o.open()) {
            int result = con.createQuery(sql)
                    .addParameter("metodo_pago", medioDePago.getMetodo_pago())
                    .addParameter("fecha_pago", medioDePago.getFecha_pago())
                    .addParameter("monto_total", medioDePago.getMonto_total())
                    .addParameter("id_pago", id)
                    .executeUpdate()
                    .getResult();

            return result > 0 ? "Medio de pago actualizado correctamente" : "No se encontró el medio de pago";
        }
    }

    @Override
    public void delete(Integer id) {
        String sql = "DELETE FROM medio_pago WHERE id_pago = :id_pago";
        try (var con = sql2o.open()) {
            con.createQuery(sql)
                    .addParameter("id_pago", id)
                    .executeUpdate();
        }
    }

    @Override
    public MedioDePago findById(Integer id) {
        String sql = "SELECT * FROM medio_pago WHERE id_pago = :id_pago";
        try (var con = sql2o.open()) {
            return con.createQuery(sql)
                    .addParameter("id_pago", id)
                    .executeAndFetchFirst(MedioDePago.class);
        }
    }

    //CONSULTA SQL COMPLEJA 6)
    @Override
    public String obtenerMetodoPagoMasUsadoEnPedidosUrgentes() {
        String sql = """
            SELECT mp.metodo_pago
            FROM medio_pago mp
            JOIN pedido p ON mp.id_pago = p.id_pedido
            WHERE p.urgente = TRUE
            GROUP BY mp.metodo_pago
            ORDER BY COUNT(*) DESC
            LIMIT 1
        """;
        try (var con = sql2o.open()) {
            return con.createQuery(sql)
                    .executeScalar(String.class);
        }
    }
}