package com.example.inventario;

import jakarta.persistence.*;

@Entity
@Table(name = "inventario")
public class InventarioItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String producto;
    private String bodega;
    private int stock;
    private double consumoPromedioDiario;
    private int puntoReposicion;

    public InventarioItem() {}

    public InventarioItem(String producto, String bodega, int stock, double consumoPromedioDiario, int puntoReposicion) {
        this.producto = producto;
        this.bodega = bodega;
        this.stock = stock;
        this.consumoPromedioDiario = consumoPromedioDiario;
        this.puntoReposicion = puntoReposicion;
    }

    public Long getId() { return id; }
    public String getProducto() { return producto; }
    public void setProducto(String producto) { this.producto = producto; }
    public String getBodega() { return bodega; }
    public void setBodega(String bodega) { this.bodega = bodega; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    public double getConsumoPromedioDiario() { return consumoPromedioDiario; }
    public void setConsumoPromedioDiario(double consumoPromedioDiario) { this.consumoPromedioDiario = consumoPromedioDiario; }
    public int getPuntoReposicion() { return puntoReposicion; }
    public void setPuntoReposicion(int puntoReposicion) { this.puntoReposicion = puntoReposicion; }
}
