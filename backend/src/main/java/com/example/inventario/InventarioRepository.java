package com.example.inventario;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InventarioRepository extends JpaRepository<InventarioItem, Long> {
    List<InventarioItem> findByProductoIgnoreCase(String producto);
}
