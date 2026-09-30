package com.example.inventario;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria")
public class Auditoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String accion;
    private String entidad;
    private Long entidadId;
    @Column(length = 1500)
    private String detalle;
    private LocalDateTime fecha = LocalDateTime.now();

    public Auditoria() {}

    public Auditoria(String accion, String entidad, Long entidadId, String detalle) {
        this.accion = accion;
        this.entidad = entidad;
        this.entidadId = entidadId;
        this.detalle = detalle;
    }

    public Long getId() { return id; }
    public String getAccion() { return accion; }
    public String getEntidad() { return entidad; }
    public Long getEntidadId() { return entidadId; }
    public String getDetalle() { return detalle; }
    public LocalDateTime getFecha() { return fecha; }
}
