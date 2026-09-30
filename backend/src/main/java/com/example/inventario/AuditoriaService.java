package com.example.inventario;

import org.springframework.stereotype.Service;

@Service
public class AuditoriaService {
    private final AuditoriaRepository repository;

    public AuditoriaService(AuditoriaRepository repository) {
        this.repository = repository;
    }

    public void registrar(String accion, String entidad, Long entidadId, String detalle) {
        repository.save(new Auditoria(accion, entidad, entidadId, detalle));
    }
}
