package com.example.demo.Repository;

import com.example.demo.DTO.PedidoCompletoDTO;
import com.example.demo.DTO.ResumenPedidoDTO;
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

    @Override
    public List<ResumenPedidoDTO> obtenerResumenPorCliente(Integer idCliente) {
        String sqlPedidos = """
        SELECT id_pedido, fecha_pedido, estado
        FROM pedido
        WHERE id_cliente = :idCliente
    """;

        String sqlProductos = """
        SELECT p.nombre_producto AS nombreProducto, dp.cantidad, dp.subtotal
        FROM detalle_de_pedido dp
        JOIN ProductoServicio p ON dp.id_producto = p.id_producto
        WHERE dp.id_pedido = :idPedido
    """;


        try (var con = sql2o.open()) {
            var pedidos = con.createQuery(sqlPedidos)
                    .addParameter("idCliente", idCliente)
                    .executeAndFetchTable()
                    .asList();

            List<ResumenPedidoDTO> resumenes = new java.util.ArrayList<>();

            for (var row : pedidos) {
                Integer idPedido = (Integer) row.get("id_pedido");
                String fecha = row.get("fecha_pedido").toString();
                String estado = (String) row.get("estado");

                var productos = con.createQuery(sqlProductos)
                        .addParameter("idPedido", idPedido)
                        .executeAndFetch(com.example.demo.DTO.ProductoCantidadDTO.class);

                double total = productos.stream().mapToDouble(p -> p.getSubtotal()).sum();

                resumenes.add(new ResumenPedidoDTO(idPedido, fecha, estado, total, productos));
            }

            return resumenes;
        }
    }


    //--------------------------------------- PROCEDIMIENTOS ALMACENADOS ------------------------------------------

    //PROCEDIMIENTO ALMACENADO 7)
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

    //PROCEDIMIENTO ALMACENADO 8)
    @Override
    public void cambiarEstadoPedido(int idPedido, String nuevoEstado) {
        try (Connection conn = sql2o.open()) {
            conn.createQuery("CALL cambiar_estado_pedido(:idPedido, :nuevoEstado)")
                    .addParameter("idPedido", idPedido)
                    .addParameter("nuevoEstado", nuevoEstado)
                    .executeUpdate(); // correcto porque el procedimiento es VOID
        } catch (Exception e) {
            throw new RuntimeException("Error al cambiar el estado del pedido", e);
        }
    }

    //PROCEDIMIENTO ALMACENADO 9)
    public void confirmarPedidoYDescontarStock(int idPedido) {
        try (Connection conn = sql2o.open()) {
            conn.createQuery("CALL confirmar_pedido_y_descontar_stock(:idPedido)")
                    .addParameter("idPedido", idPedido)
                    .executeUpdate(); // correcto para funciones/procedimientos VOID
        } catch (Exception e) {
            throw new RuntimeException("Error al confirmar el pedido y descontar stock", e);
        }
    }


    // Método para obtener los pedidos por id_cliente
    @Override
    public List<Pedido> getPedidosByCliente(Integer idCliente) {
        String sql = "SELECT * FROM Pedido WHERE id_cliente = :id_cliente"; // Query para obtener pedidos por id_cliente
        try (var con = sql2o.open()) {
            return con.createQuery(sql)
                    .addParameter("id_cliente", idCliente) // Agregar el parámetro del id_cliente
                    .executeAndFetch(Pedido.class); // Ejecutar la consulta y mapear a la entidad Pedido
        }
    }



}
