package com.example.demo.Repository;

import com.example.demo.Entity.MedioDePago;

import java.util.List;

public interface MediosDePagoRepository {
    public MedioDePago crear(MedioDePago medioPago);
    public List<MedioDePago> getAll();
    public String update(MedioDePago medioPago, Integer id);
    public void delete(Integer id);
}
