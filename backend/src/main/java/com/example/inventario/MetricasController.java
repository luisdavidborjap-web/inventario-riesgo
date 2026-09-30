package com.example.inventario;

import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/metricas")
public class MetricasController {
    private final InventarioRepository inventarioRepository;
    private final RecomendacionRepository recomendacionRepository;

    public MetricasController(InventarioRepository inventarioRepository, RecomendacionRepository recomendacionRepository) {
        this.inventarioRepository = inventarioRepository;
        this.recomendacionRepository = recomendacionRepository;
    }

    @GetMapping
    public Map<String, Object> metricas() {
        var inventario = inventarioRepository.findAll();
        var recomendaciones = recomendacionRepository.findAll();

        Map<String, Long> quiebresPorProducto = inventario.stream()
                .filter(i -> i.getStock() <= 0)
                .collect(Collectors.groupingBy(InventarioItem::getProducto, Collectors.counting()));

        double tiempoAprobacionPromedioMin = recomendaciones.stream()
                .filter(r -> r.getDecididaEn() != null)
                .mapToLong(r -> Duration.between(r.getCreadaEn(), r.getDecididaEn()).toMinutes())
                .average()
                .orElse(0);

        long comprasUrgentes = recomendaciones.stream()
                .filter(r -> r.getTipo() == Recomendacion.Tipo.COMPRA)
                .filter(r -> "ALTO".equals(r.getRiesgo()))
                .count();

        long decididas = recomendaciones.stream()
                .filter(r -> r.getEstado() != Recomendacion.Estado.PENDIENTE)
                .count();

        long aceptadas = recomendaciones.stream()
                .filter(r -> r.getEstado() == Recomendacion.Estado.APROBADA
                        || r.getEstado() == Recomendacion.Estado.EJECUTADA)
                .count();

        double porcentajeAceptadas = decididas == 0 ? 0 : (aceptadas * 100.0 / decididas);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("quiebresPorProducto", quiebresPorProducto);
        result.put("tiempoAprobacionPromedioMinutos", tiempoAprobacionPromedioMin);
        result.put("comprasUrgentes", comprasUrgentes);
        result.put("recomendacionesAceptadasPorcentaje", porcentajeAceptadas);
        return result;
    }
}
