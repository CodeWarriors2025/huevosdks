package com.huevosdks.repository;

import com.huevosdks.entity.BitacoraInventario;
import com.huevosdks.entity.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BitacoraInventarioRepository extends JpaRepository<BitacoraInventario, Long> {

    List<BitacoraInventario> findByInventarioOrderByFechaDesc(Inventario inventario);
}