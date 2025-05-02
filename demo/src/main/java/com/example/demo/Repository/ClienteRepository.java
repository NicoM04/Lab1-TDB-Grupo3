package com.example.demo.Repository;

import com.example.demo.Entity.Cliente;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ClienteRepository {
    public ResponseEntity<Object> crear(Cliente cliente);
    public List<Cliente> getAll();
    public String update(Cliente cliente, Integer id);
    public void delete(Integer id);
    public Cliente findById(Integer id);

    public ResponseEntity<Cliente> findByCorreo(String correo);
    public ResponseEntity<Cliente> findByName(String name);
    public ResponseEntity<Object> loginUser(String correo_cliente, String contrasena_cliente);

}
