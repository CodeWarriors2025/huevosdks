package com.huevosdks.repository;

import com.huevosdks.entity.Inventario;
import com.huevosdks.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventarioRepository extends JpaRepository<Inventario, Long> {

    // Busca el inventario de un producto específico
    Optional<Inventario> findByProducto(Producto producto);
}