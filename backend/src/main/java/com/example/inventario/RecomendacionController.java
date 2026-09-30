package com.example.inventario;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/recomendaciones")
public class RecomendacionController {
    private final RecomendacionRepository repository;
    private final RecomendacionService service;

    public RecomendacionController(RecomendacionRepository repository, RecomendacionService service) {
        this.repository = repository;
        this.service = service;
    }

    @GetMapping
    public List<Recomendacion> listar() {
        return repository.findAllByOrderByCreadaEnDesc();
    }

    @PostMapping("/generar")
    public List<Recomendacion> generar() {
        return service.generar();
    }

    @PostMapping("/{id}/aprobar")
    public Recomendacion aprobar(@PathVariable Long id) {
        return service.aprobar(id);
    }

    @PostMapping("/{id}/rechazar")
    public Recomendacion rechazar(@PathVariable Long id) {
        return service.rechazar(id);
    }
}
