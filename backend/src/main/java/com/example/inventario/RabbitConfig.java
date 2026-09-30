package com.example.inventario;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    @Bean
    Queue comprasAprobadasQueue() {
        return new Queue("compras.aprobadas", true);
    }
}
