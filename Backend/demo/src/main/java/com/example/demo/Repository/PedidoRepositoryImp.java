package com.example.demo.Repository;

import com.example.demo.DTO.PedidoCompletoDTO;
import com.example.demo.Entity.Pedido;
import com.example.demo.Repository.PedidoRepository;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Array;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@Repository
public class PedidoRepositoryImp implements PedidoRepository {

    @Autowired
    private Sql2o sql2o;

    @Override
    public Pedido crear(Pedido pedido) {
        String sql = "INSERT INTO Pedido (id_cliente, id_empresa, id_repartidor, id_pago, fecha_pedido, fecha_entrega, estado, urgente) " +
                "VALUES (:id_cliente, :id_empresa, :id_repartidor, :id_pago, :fecha_pedido, :fecha_entrega, :estado, :urgente)";
        try (var con = sql2o.open()) {
            con.createQuery(sql)
                    .addParameter("id_cliente", pedido.getId_cliente())
                    .addParameter("id_empresa", pedido.getId_empresa())
                    .addParameter("id_repartidor", pedido.getId_repartidor())
                    .addParameter("id_pago", pedido.getId_pago())
                    .addParameter("fecha_pedido", pedido.getFecha_pedido())
                    .addParameter("fecha_entrega", pedido.getFecha_entrega())
                    .addParameter("estado", pedido.getEstado())
                    .addParameter("urgente", pedido.getUrgente())
                    .executeUpdate();
            return pedido;
        }
    }

    @Override
    public List<Pedido> getAll() {
        String sql = "SELECT * FROM Pedido";
        try (var con = sql2o.open()) {
            return con.createQuery(sql)
                    .executeAndFetch(Pedido.class);
        }
    }

    @Override
    public Pedido getById(Integer id) {
        String sql = "SELECT * FROM Pedido WHERE id_pedido = :id_pedido";
        try (var con = sql2o.open()) {
            return con.createQuery(sql)
                    .addParameter("id_pedido", id)
                    .executeAndFetchFirst(Pedido.class);  // Devuelve el primer resultado o null si no existe
        }
    }

    @Override
    public String update(Pedido pedido, Integer id) {
        String sql = "UPDATE Pedido SET id_cliente = :id_cliente, id_empresa = :id_empresa, id_repartidor = :id_repartidor, " +
                "id_pago = :id_pago, fecha_pedido = :fecha_pedido, fecha_entrega = :fecha_entrega, estado = :estado, urgente = :urgente " +
                "WHERE id_pedido = :id_pedido";
        try (var con = sql2o.open()) {
            int affectedRows = con.createQuery(sql)
                    .addParameter("id_cliente", pedido.getId_cliente())
                    .addParameter("id_empresa", pedido.getId_empresa())
                    .addParameter("id_repartidor", pedido.getId_repartidor())
                    .addParameter("id_pago", pedido.getId_pago())
                    .addParameter("fecha_pedido", pedido.getFecha_pedido())
                    .addParameter("fecha_entrega", pedido.getFecha_entrega())
                    .addParameter("estado", pedido.getEstado())
                    .addParameter("urgente", pedido.getUrgente())
                    .addParameter("id_pedido", id)
                    .executeUpdate()
                    .getResult();

            return affectedRows > 0 ? "Pedido actualizado exitosamente" : "No se encontró el pedido para actualizar";
        }
    }

    @Override
    public void delete(Integer id) {
        String sql = "DELETE FROM Pedido WHERE id_pedido = :id_pedido";
        try (var con = sql2o.open()) {
            con.createQuery(sql)
                    .addParameter("id_pedido", id)
                    .executeUpdate();
        }
    }

    public void registrarPedidoCompleto(PedidoCompletoDTO dto) {
        try (Connection conn = sql2o.open()) {
            java.sql.Connection jdbcConn = conn.getJdbcConnection();

            Array productosArray = jdbcConn.createArrayOf("INTEGER", dto.getProductos());
            Array cantidadesArray = jdbcConn.createArrayOf("INTEGER", dto.getCantidades());

            conn.createQuery("CALL registrar_pedido_completo(:idCliente, :idEmpresa, :idRepartidor, :fechaPedido, :fechaEntrega, :estado, :urgente, :metodoPago, :productos, :cantidades)")
                    .addParameter("idCliente", dto.getIdCliente())
                    .addParameter("idEmpresa", dto.getIdEmpresa())
                    .addParameter("idRepartidor", dto.getIdRepartidor())
                    .addParameter("fechaPedido", dto.getFechaPedido())
                    .addParameter("fechaEntrega", dto.getFechaEntrega())
                    .addParameter("estado", dto.getEstado())
                    .addParameter("urgente", dto.getUrgente())
                    .addParameter("metodoPago", dto.getMetodoPago())
                    .addParameter("productos", productosArray)
                    .addParameter("cantidades", cantidadesArray)
                    .executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al registrar pedido completo", e);
        }
    }

    public void confirmarPedidoYDescontarStock(int idPedido) {
        try (Connection conn = sql2o.open()) {
            conn.createQuery("CALL confirmar_pedido_y_descontar_stock(:idPedido)")
                    .addParameter("idPedido", idPedido)
                    .executeUpdate(); // correcto para funciones/procedimientos VOID
        } catch (Exception e) {
            throw new RuntimeException("Error al confirmar el pedido y descontar stock", e);
        }
    }


}
