package com.example.inventario;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/inventario")
public class InventarioController {
    private final InventarioRepository repository;
    private final AuditoriaService auditoriaService;

    public InventarioController(InventarioRepository repository, AuditoriaService auditoriaService) {
        this.repository = repository;
        this.auditoriaService = auditoriaService;
    }

    @GetMapping
    public List<InventarioItem> listar() {
        return repository.findAll();
    }

    @PostMapping
    public InventarioItem crear(@RequestBody InventarioItem item) {
        InventarioItem guardado = repository.save(item);
        auditoriaService.registrar("INVENTARIO_CREADO", "InventarioItem", guardado.getId(),
                guardado.getProducto() + " en " + guardado.getBodega());
        return guardado;
    }

    @PutMapping("/{id}")
    public InventarioItem actualizar(@PathVariable Long id, @RequestBody InventarioItem datos) {
        InventarioItem item = repository.findById(id).orElseThrow();
        item.setProducto(datos.getProducto());
        item.setBodega(datos.getBodega());
        item.setStock(datos.getStock());
        item.setConsumoPromedioDiario(datos.getConsumoPromedioDiario());
        item.setPuntoReposicion(datos.getPuntoReposicion());
        InventarioItem guardado = repository.save(item);
        auditoriaService.registrar("INVENTARIO_ACTUALIZADO", "InventarioItem", guardado.getId(),
                "Stock actualizado a " + guardado.getStock());
        return guardado;
    }
}
