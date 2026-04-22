package com.huevosdks.repository;

import com.huevosdks.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Busca un usuario por teléfono (usado en el login)
    Optional<Usuario> findByTelefono(String telefono);

    // Verifica si ya existe un usuario con ese teléfono (usado en el registro)
    boolean existsByTelefono(String telefono);
}