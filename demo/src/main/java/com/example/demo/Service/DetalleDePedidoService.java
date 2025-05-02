package com.example.demo.Service;

import com.example.demo.Entity.DetalleDePedido;
import com.example.demo.Repository.DetalleDePedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DetalleDePedidoService {

    @Autowired
    private DetalleDePedidoRepository detalleDePedidoRepository;

    public DetalleDePedido crearDetalle(DetalleDePedido detalle) {
        return detalleDePedidoRepository.crear(detalle);
    }

    public List<DetalleDePedido> obtenerTodos() {
        return detalleDePedidoRepository.getAll();
    }

    public DetalleDePedido getById(Integer id) {
        return detalleDePedidoRepository.getById(id);
    }

    public String actualizarDetalle(DetalleDePedido detalle, Integer id) {
        return detalleDePedidoRepository.update(detalle, id);
    }

    public void eliminarDetalle(Integer id) {
        detalleDePedidoRepository.delete(id);
    }
}
