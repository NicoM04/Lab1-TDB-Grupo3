package com.example.demo.Repository;

import com.example.demo.Entity.Repartidor;

import java.util.List;

public interface RepartidoresRepository {
    public Repartidor crear(Repartidor repartidor);
    public List<Repartidor> getAll();
    public String update(Repartidor repartidor, Integer id);
    public void delete(Integer id);
}
