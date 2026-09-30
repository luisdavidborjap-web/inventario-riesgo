package com.example.inventario;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class RecomendacionService {
    private final InventarioRepository inventarioRepository;
    private final RecomendacionRepository recomendacionRepository;
    private final PronosticoService pronosticoService;
    private final AuditoriaService auditoriaService;
    private final ComprasIntegrationService comprasIntegrationService;

    public RecomendacionService(
            InventarioRepository inventarioRepository,
            RecomendacionRepository recomendacionRepository,
            PronosticoService pronosticoService,
            AuditoriaService auditoriaService,
            ComprasIntegrationService comprasIntegrationService
    ) {
        this.inventarioRepository = inventarioRepository;
        this.recomendacionRepository = recomendacionRepository;
        this.pronosticoService = pronosticoService;
        this.auditoriaService = auditoriaService;
        this.comprasIntegrationService = comprasIntegrationService;
    }

    @Transactional
    public List<Recomendacion> generar() {
        List<Recomendacion> creadas = new ArrayList<>();

        for (InventarioItem item : inventarioRepository.findAll()) {
            PronosticoService.ResultadoPronostico p;
            try {
                p = pronosticoService.pronosticar(item);
            } catch (Exception ex) {
                p = pronosticoService.fallback(item);
                auditoriaService.registrar(
                        "FALLBACK_PRONOSTICO",
                        "InventarioItem",
                        item.getId(),
                        "Se utilizó el cálculo de respaldo porque el pronóstico no estuvo disponible."
                );
            }

            if ("BAJO".equals(p.riesgo())) continue;

            boolean yaPendiente = recomendacionRepository.findAll().stream()
                    .anyMatch(r -> r.getInventarioId().equals(item.getId())
                            && r.getEstado() == Recomendacion.Estado.PENDIENTE);
            if (yaPendiente) continue;

            Recomendacion r = construirRecomendacion(item, p);
            recomendacionRepository.save(r);
            auditoriaService.registrar(
                    "RECOMENDACION_GENERADA",
                    "Recomendacion",
                    r.getId(),
                    r.getTipo() + " - " + r.getProducto() + " - riesgo " + r.getRiesgo()
            );
            creadas.add(r);
        }
        return creadas;
    }

    private Recomendacion construirRecomendacion(InventarioItem destino, PronosticoService.ResultadoPronostico p) {
        int objetivo = Math.max(destino.getPuntoReposicion() * 2,
                (int)Math.ceil(destino.getConsumoPromedioDiario() * 7));
        int faltante = Math.max(1, objetivo - destino.getStock());

        InventarioItem origen = inventarioRepository.findByProductoIgnoreCase(destino.getProducto()).stream()
                .filter(x -> !x.getId().equals(destino.getId()))
                .filter(x -> x.getStock() > Math.max(x.getPuntoReposicion(), faltante))
                .max(Comparator.comparingInt(InventarioItem::getStock))
                .orElse(null);

        Recomendacion r = new Recomendacion();
        r.setInventarioId(destino.getId());
        r.setProducto(destino.getProducto());
        r.setBodegaDestino(destino.getBodega());
        r.setCantidad(faltante);
        r.setRiesgo(p.riesgo());
        r.setUsoFallback(p.fallback());

        if (origen != null) {
            r.setTipo(Recomendacion.Tipo.TRANSFERENCIA);
            r.setBodegaOrigen(origen.getBodega());
            r.setMotivo("Hay stock suficiente en otra bodega; se recomienda transferir antes de comprar.");
        } else {
            r.setTipo(Recomendacion.Tipo.COMPRA);
            r.setMotivo("No existe otra bodega con excedente suficiente; se recomienda compra.");
        }

        return r;
    }

    @Transactional
    public Recomendacion aprobar(Long id) {
        Recomendacion r = recomendacionRepository.findById(id).orElseThrow();
        if (r.getEstado() != Recomendacion.Estado.PENDIENTE) {
            throw new IllegalStateException("La recomendación ya fue decidida");
        }

        r.setEstado(Recomendacion.Estado.APROBADA);
        r.setDecididaEn(LocalDateTime.now());
        recomendacionRepository.save(r);

        auditoriaService.registrar("RECOMENDACION_APROBADA", "Recomendacion", r.getId(),
                "Aprobada por una persona antes de su ejecución.");

        if (r.getTipo() == Recomendacion.Tipo.TRANSFERENCIA) {
            ejecutarTransferencia(r);
        } else {
            comprasIntegrationService.publicarCompraAprobada(r);
            r.setEstado(Recomendacion.Estado.EJECUTADA);
            recomendacionRepository.save(r);
            auditoriaService.registrar("COMPRA_PUBLICADA", "Recomendacion", r.getId(),
                    "Evento publicado en RabbitMQ para el sistema de compras existente.");
        }

        return r;
    }

    @Transactional
    public Recomendacion rechazar(Long id) {
        Recomendacion r = recomendacionRepository.findById(id).orElseThrow();
        if (r.getEstado() != Recomendacion.Estado.PENDIENTE) {
            throw new IllegalStateException("La recomendación ya fue decidida");
        }
        r.setEstado(Recomendacion.Estado.RECHAZADA);
        r.setDecididaEn(LocalDateTime.now());
        recomendacionRepository.save(r);
        auditoriaService.registrar("RECOMENDACION_RECHAZADA", "Recomendacion", r.getId(),
                "La recomendación fue rechazada por una persona.");
        return r;
    }

    private void ejecutarTransferencia(Recomendacion r) {
        InventarioItem destino = inventarioRepository.findById(r.getInventarioId()).orElseThrow();
        InventarioItem origen = inventarioRepository.findByProductoIgnoreCase(r.getProducto()).stream()
                .filter(x -> x.getBodega().equals(r.getBodegaOrigen()))
                .findFirst()
                .orElseThrow();

        int cantidad = Math.min(r.getCantidad(), origen.getStock());
        origen.setStock(origen.getStock() - cantidad);
        destino.setStock(destino.getStock() + cantidad);
        inventarioRepository.save(origen);
        inventarioRepository.save(destino);

        r.setEstado(Recomendacion.Estado.EJECUTADA);
        recomendacionRepository.save(r);

        auditoriaService.registrar("TRANSFERENCIA_EJECUTADA", "Recomendacion", r.getId(),
                "Se transfirieron " + cantidad + " unidades de " + r.getBodegaOrigen() +
                        " a " + r.getBodegaDestino() + ".");
    }
}
