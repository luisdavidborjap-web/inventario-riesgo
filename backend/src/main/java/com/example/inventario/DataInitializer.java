package com.example.inventario;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner cargarDatos(InventarioRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                repository.save(new InventarioItem("Pollo", "Bodega Centro", 18, 10, 20));
                repository.save(new InventarioItem("Pollo", "Bodega Norte", 120, 8, 25));
                repository.save(new InventarioItem("Arroz", "Bodega Centro", 12, 9, 18));
                repository.save(new InventarioItem("Arroz", "Bodega Norte", 15, 7, 18));
                repository.save(new InventarioItem("Aceite", "Bodega Centro", 45, 4, 15));
            }
        };
    }
}
