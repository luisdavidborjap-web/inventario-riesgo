package com.example.inventario;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PronosticoService {

    @Value("${app.forecast.enabled:true}")
    private boolean enabled;

    public ResultadoPronostico pronosticar(InventarioItem item) {
        if (!enabled) {
            throw new IllegalStateException("Servicio de pronóstico no disponible");
        }

        double consumo = Math.max(item.getConsumoPromedioDiario(), 0.1);
        double diasCobertura = item.getStock() / consumo;
        String riesgo = diasCobertura <= 2 ? "ALTO" : diasCobertura <= 5 ? "MEDIO" : "BAJO";
        return new ResultadoPronostico(diasCobertura, riesgo, false);
    }

    public ResultadoPronostico fallback(InventarioItem item) {
        double consumo = Math.max(item.getConsumoPromedioDiario(), 1.0);
        double diasCobertura = item.getStock() / consumo;
        String riesgo = item.getStock() <= item.getPuntoReposicion() || diasCobertura <= 2
                ? "ALTO"
                : diasCobertura <= 5 ? "MEDIO" : "BAJO";
        return new ResultadoPronostico(diasCobertura, riesgo, true);
    }

    public record ResultadoPronostico(double diasCobertura, String riesgo, boolean fallback) {}
}
