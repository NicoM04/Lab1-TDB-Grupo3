package com.example.demo.Service;

import com.example.demo.Entity.Cliente;
import com.example.demo.Repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    // Crear un cliente
    public ResponseEntity<Object> crearCliente(Cliente cliente) {
        return clienteRepository.crear(cliente);
    }

    // Obtener todos los clientes
    public List<Cliente> obtenerTodosLosClientes() {
        return clienteRepository.getAll();
    }

    // Obtener un cliente por su ID
    public Cliente obtenerClientePorId(Integer id) {
        return clienteRepository.findById(id);
    }

    // Actualizar un cliente
    public String actualizarCliente(Integer id, Cliente cliente) {
        return clienteRepository.update(cliente, id);
    }

    // Eliminar un cliente
    public void eliminarCliente(Integer id) {
        clienteRepository.delete(id);
    }

    public ResponseEntity<Object> loginUser(String email, String password) {
        return clienteRepository.loginUser(email, password);
    }
}
