package com.example.demo.Repository;

import com.example.demo.Entity.EmpresasAsociadas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.List;

@Repository
public class EmpresasAsociadasRepositoryImp implements EmpresasAsociadasRepository {

    @Autowired
    private Sql2o sql2o;

    @Override
    public EmpresasAsociadas crear(EmpresasAsociadas empresa) {
        try (Connection conn = sql2o.open()) {
            String sql = "INSERT INTO empresas_asociadas (nombre_empresa, rut_empresa, correo_contacto, direccion) " +
                    "VALUES (:nombre_empresa, :rut_empresa, :correo_contacto, :direccion)";
            conn.createQuery(sql)
                    .addParameter("nombre_empresa", empresa.getNombre_empresa())
                    .addParameter("rut_empresa", empresa.getRut_empresa())
                    .addParameter("correo_contacto", empresa.getCorreo_contacto())
                    .addParameter("direccion", empresa.getDireccion())
                    .executeUpdate();
            return empresa;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    @Override
    public List<EmpresasAsociadas> getAll() {
        try (Connection conn = sql2o.open()) {
            String sql = "SELECT * FROM empresas_asociadas";
            return conn.createQuery(sql)
                    .executeAndFetch(EmpresasAsociadas.class);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    @Override
    public String update(EmpresasAsociadas empresa, Integer id) {
        try (Connection conn = sql2o.open()) {
            String sql = "UPDATE empresas_asociadas SET nombre_empresa = :nombre_empresa, rut_empresa = :rut_empresa, " +
                    "correo_contacto = :correo_contacto, direccion = :direccion WHERE id_empresa = :id";
            conn.createQuery(sql)
                    .addParameter("nombre_empresa", empresa.getNombre_empresa())
                    .addParameter("rut_empresa", empresa.getRut_empresa())
                    .addParameter("correo_contacto", empresa.getCorreo_contacto())
                    .addParameter("direccion", empresa.getDireccion())
                    .addParameter("id", id)
                    .executeUpdate();
            return "Actualizado correctamente";
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    @Override
    public void delete(Integer id) {
        try (Connection conn = sql2o.open()) {
            String sql = "DELETE FROM empresas_asociadas WHERE id_empresa = :id";
            conn.createQuery(sql)
                    .addParameter("id", id)
                    .executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<EmpresasAsociadas> getEmpresasConMasFallos() {
        try (Connection conn = sql2o.open()) {
            String sql = """
            SELECT e.*
            FROM empresas_asociadas e
            JOIN pedido p ON e.id_empresa = p.id_empresa
            WHERE p.estado = 'fallido'
            GROUP BY e.id_empresa
            ORDER BY COUNT(p.id_pedido) DESC
            """;

            return conn.createQuery(sql)
                    .executeAndFetch(EmpresasAsociadas.class);
        } catch (Exception e) {
            System.out.println("Error al obtener empresas con más fallos: " + e.getMessage());
            return null;
        }
    }

}
