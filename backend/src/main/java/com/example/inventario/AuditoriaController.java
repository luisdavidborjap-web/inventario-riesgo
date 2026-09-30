package com.example.inventario;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/auditoria")
public class AuditoriaController {
    private final AuditoriaRepository repository;

    public AuditoriaController(AuditoriaRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Auditoria> listar() {
        return repository.findAllByOrderByFechaDesc();
    }
}
