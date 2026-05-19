package com.huevosdks.repository;

import com.huevosdks.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByActivoTrue();

    List<Producto> findByActivoTrueAndTipo(Producto.TipoProducto tipo);

    List<Producto> findAllByOrderByNombreAsc();

    Optional<Producto> findByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCase(String nombre);
}
