package com.example.demo.Repository;

import com.example.demo.Entity.Cliente;
import com.example.demo.config.InputVerificationService;
import com.example.demo.config.JwtMiddlewareService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ClienteRepositoryImp implements ClienteRepository {

    @Autowired
    private Sql2o sql2o;

    @Autowired
    private JwtMiddlewareService jwtMiddlewareService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public ResponseEntity<Object> crear(Cliente cliente) {
        // Verificación de entrada (validación de caracteres)
        if (!InputVerificationService.validateInput(cliente.getNombre_cliente()) ||
                !InputVerificationService.validateInput(cliente.getCorreo_cliente()) ||
                !InputVerificationService.validateInput(cliente.getContrasena_cliente())) {
            return ResponseEntity.badRequest().body("Error al crear el usuario: caracteres no permitidos.");
        }

        // Verificación si ya existe un usuario con el mismo nombre o correo
        String checkQuery = "SELECT COUNT(*) FROM Cliente WHERE correo_cliente = :correo_cliente OR nombre_cliente = :nombre_cliente";
        try (var con = sql2o.open()) {
            Integer count = con.createQuery(checkQuery)
                    .addParameter("correo_cliente", cliente.getCorreo_cliente())
                    .addParameter("nombre_cliente", cliente.getNombre_cliente())
                    .executeScalar(Integer.class);

            if (count != null && count > 0) {
                return ResponseEntity.status(409).body("Ya existe un usuario con el mismo nombre o correo.");
            }

            String hashedPassword = passwordEncoder.encode(cliente.getContrasena_cliente());

            // Inserción del nuevo cliente
            String insertQuery = "INSERT INTO Cliente (nombre_cliente, contrasena_cliente, correo_cliente, direccion, telefono, fecha_registro) " +
                    "VALUES (:nombre_cliente, :contrasena_cliente, :correo_cliente, :direccion, :telefono, :fecha_registro)";

            // Se insertan los datos del cliente
            con.createQuery(insertQuery)
                    .addParameter("nombre_cliente", cliente.getNombre_cliente())
                    .addParameter("contrasena_cliente", hashedPassword)
                    .addParameter("correo_cliente", cliente.getCorreo_cliente())
                    .addParameter("direccion", cliente.getDireccion())
                    .addParameter("telefono", cliente.getTelefono())
                    .addParameter("fecha_registro", cliente.getFecha_registro())
                    .executeUpdate();

            // Ahora generamos el token JWT para el cliente recién creado
            String token = jwtMiddlewareService.generateToken(cliente); // Generar token JWT

            // Devolver el token como respuesta
            return ResponseEntity.ok(token);

        } catch (Exception e) {
            // Manejo de excepciones
            return ResponseEntity.status(500).body("Error al crear el usuario: " + e.getMessage());
        }
    }


    @Override
    public List<Cliente> getAll() {
        String sql = "SELECT * FROM Cliente";
        try (var con = sql2o.open()) {
            return con.createQuery(sql)
                    .executeAndFetch(Cliente.class);
        }
    }

    @Override
    public String update(Cliente cliente, Integer id) {
        // SQL modificado para actualizar solo los campos que deseas
        String sql = "UPDATE Cliente SET nombre_cliente = :nombre_cliente, telefono = :telefono, correo_cliente = :correo_cliente, direccion = :direccion " +
                "WHERE id_cliente = :id_cliente";
        try (var con = sql2o.open()) {
            // Ejecuta la actualización, pero solo con los parámetros que se deben actualizar
            int affectedRows = con.createQuery(sql)
                    .addParameter("nombre_cliente", cliente.getNombre_cliente())
                    .addParameter("telefono", cliente.getTelefono())  // Solo se actualiza el teléfono
                    .addParameter("correo_cliente", cliente.getCorreo_cliente())  // Solo se actualiza el correo
                    .addParameter("direccion", cliente.getDireccion())  // Solo se actualiza la dirección
                    .addParameter("id_cliente", id)  // Se busca por el ID
                    .executeUpdate()
                    .getResult();

            // Devolver el mensaje correspondiente según el resultado
            return affectedRows > 0 ? "Cliente actualizado exitosamente" : "No se encontró el cliente para actualizar";
        }
    }

    @Override
    public ResponseEntity<Object> loginUser(String correo_cliente, String contrasena_cliente) {
        if (!InputVerificationService.validateInput(correo_cliente) || !InputVerificationService.validateInput(contrasena_cliente)) {
            return ResponseEntity.badRequest().body("Error al iniciar sesión: caracteres no permitidos.");
        }

        try {
            ResponseEntity<Cliente> response = findByCorreo(correo_cliente);
            Cliente cliente = response.getBody();

            if (cliente == null) {
                return ResponseEntity.status(401).body("Usuario no encontrado.");
            }

            if (passwordEncoder.matches(contrasena_cliente, cliente.getContrasena_cliente())) {
                String token = jwtMiddlewareService.generateToken(cliente);
                return ResponseEntity.ok(token);
            } else {
                return ResponseEntity.status(401).body("Contraseña incorrecta.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al iniciar sesión: " + e.getMessage());
        }
    }



    @Override
    public void delete(Integer id) {
        String sql = "DELETE FROM Cliente WHERE id_cliente = :id_cliente";
        try (var con = sql2o.open()) {
            con.createQuery(sql)
                    .addParameter("id_cliente", id)
                    .executeUpdate();
        }
    }

    @Override
    public Cliente findById(Integer id) {
        String sql = "SELECT * FROM Cliente WHERE id_cliente = :id_cliente";
        try (var con = sql2o.open()) {
            return con.createQuery(sql)
                    .addParameter("id_cliente", id)
                    .executeAndFetchFirst(Cliente.class); // Devuelve el primer resultado o null si no existe.
        }
    }

    @Override
    public ResponseEntity<Cliente> findByCorreo(String correo) {
        try (Connection conn = sql2o.open()) {
            Cliente cliente = conn.createQuery("SELECT * FROM cliente WHERE correo_cliente = :correo")
                    .addParameter("correo", correo)
                    .executeAndFetchFirst(Cliente.class);
            if (cliente == null) {
                return ResponseEntity.status(404).body(null);  // Cliente no encontrado
            }
            return ResponseEntity.ok(cliente);  // Cliente encontrado
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);  // Error interno del servidor
        }
    }

    @Override
    public ResponseEntity<Cliente> findByName(String name) {
        try (Connection conn = sql2o.open()) {
            // Asegúrate de que la entidad sea Cliente, no ClienteEntity
            Cliente cliente = conn.createQuery("SELECT * FROM cliente WHERE nombre_cliente = :name")
                    .addParameter("name", name)
                    .executeAndFetchFirst(Cliente.class);

            // Si no se encuentra el cliente, se retorna un error 404
            if (cliente == null) {
                return ResponseEntity.status(404).body(null);
            }

            // Si el cliente se encuentra, se retorna con un status 200
            return ResponseEntity.ok(cliente);
        } catch (Exception e) {
            // Si hay una excepción, se maneja con un error 500
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    @Override
    public Cliente getClienteMayorGasto() {
        try (var con = sql2o.open()) {
            String sql = """
            SELECT c.* 
            FROM cliente c 
            JOIN pedido p ON c.id_cliente = p.id_cliente 
            JOIN medio_pago m ON p.id_pedido = m.id_pedido 
            WHERE p.estado = 'entregado' 
            GROUP BY c.id_cliente 
            ORDER BY SUM(m.monto_total) DESC 
            LIMIT 1
            """;

            return con.createQuery(sql)
                    .executeAndFetchFirst(Cliente.class);
        } catch (Exception e) {
            System.out.println("Error al obtener cliente con mayor gasto: " + e.getMessage());
            return null;
        }
    }


}
