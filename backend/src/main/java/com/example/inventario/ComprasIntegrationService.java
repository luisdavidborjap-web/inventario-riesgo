package com.example.inventario;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ComprasIntegrationService {
    private final RabbitTemplate rabbitTemplate;

    public ComprasIntegrationService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publicarCompraAprobada(Recomendacion r) {
        rabbitTemplate.convertAndSend(
                "",
                "compras.aprobadas",
                Map.of(
                        "recomendacionId", r.getId(),
                        "producto", r.getProducto(),
                        "bodega", r.getBodegaDestino(),
                        "cantidad", r.getCantidad()
                )
        );
    }
}
