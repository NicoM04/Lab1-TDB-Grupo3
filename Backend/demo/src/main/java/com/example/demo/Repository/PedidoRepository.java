package com.example.demo.Repository;

import com.example.demo.DTO.PedidoCompletoDTO;
import com.example.demo.Entity.Pedido;

import java.util.Date;
import java.util.List;

public interface PedidoRepository {
    public Pedido crear(Pedido pedido);
    public List<Pedido> getAll();
    public String update(Pedido pedido, Integer id);
    public void delete(Integer id);
    public Pedido getById(Integer id);
    public void registrarPedidoCompleto(PedidoCompletoDTO pedidoCompletoDTO);
    void confirmarPedidoYDescontarStock(int id);

}
