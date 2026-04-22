package com.huevosdks.repository;

import com.huevosdks.entity.Cliente;
import com.huevosdks.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

// @Repository no es necesario escribirlo aquí porque JpaRepository ya lo incluye implícitamente.
// JpaRepository<Cliente, Long> significa: "repositorio para la entidad Cliente, cuya clave primaria es Long"
// Spring genera automáticamente: save(), findById(), findAll(), delete(), etc.
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    // Spring lee el nombre del método y genera el SQL solo:
    // SELECT * FROM clientes WHERE usuario_id = ?
    Cliente findByUsuario(Usuario usuario);

    // Verifica si ya existe un cliente con ese usuario (útil para evitar duplicados)
    boolean existsByUsuario(Usuario usuario);
}