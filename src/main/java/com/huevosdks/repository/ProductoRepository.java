package com.huevosdks.repository;

import com.huevosdks.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Retorna solo los productos marcados como activos
    List<Producto> findByActivoTrue();

    // Retorna productos activos filtrados por tipo de presentación
    List<Producto> findByActivoTrueAndTipo(Producto.TipoProducto tipo);
}
