package com.example.inventario;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RecomendacionRepository extends JpaRepository<Recomendacion, Long> {
    List<Recomendacion> findAllByOrderByCreadaEnDesc();
}
