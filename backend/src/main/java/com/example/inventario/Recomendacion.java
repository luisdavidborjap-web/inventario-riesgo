package com.example.inventario;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "recomendaciones")
public class Recomendacion {
    public enum Tipo { TRANSFERENCIA, COMPRA }
    public enum Estado { PENDIENTE, APROBADA, RECHAZADA, EJECUTADA }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long inventarioId;
    private String producto;
    private String bodegaOrigen;
    private String bodegaDestino;

    @Enumerated(EnumType.STRING)
    private Tipo tipo;

    private int cantidad;
    private String riesgo;
    private String motivo;
    private boolean usoFallback;

    @Enumerated(EnumType.STRING)
    private Estado estado = Estado.PENDIENTE;

    private LocalDateTime creadaEn = LocalDateTime.now();
    private LocalDateTime decididaEn;

    public Recomendacion() {}

    public Long getId() { return id; }
    public Long getInventarioId() { return inventarioId; }
    public void setInventarioId(Long inventarioId) { this.inventarioId = inventarioId; }
    public String getProducto() { return producto; }
    public void setProducto(String producto) { this.producto = producto; }
    public String getBodegaOrigen() { return bodegaOrigen; }
    public void setBodegaOrigen(String bodegaOrigen) { this.bodegaOrigen = bodegaOrigen; }
    public String getBodegaDestino() { return bodegaDestino; }
    public void setBodegaDestino(String bodegaDestino) { this.bodegaDestino = bodegaDestino; }
    public Tipo getTipo() { return tipo; }
    public void setTipo(Tipo tipo) { this.tipo = tipo; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public String getRiesgo() { return riesgo; }
    public void setRiesgo(String riesgo) { this.riesgo = riesgo; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public boolean isUsoFallback() { return usoFallback; }
    public void setUsoFallback(boolean usoFallback) { this.usoFallback = usoFallback; }
    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }
    public LocalDateTime getCreadaEn() { return creadaEn; }
    public void setCreadaEn(LocalDateTime creadaEn) { this.creadaEn = creadaEn; }
    public LocalDateTime getDecididaEn() { return decididaEn; }
    public void setDecididaEn(LocalDateTime decididaEn) { this.decididaEn = decididaEn; }
}
