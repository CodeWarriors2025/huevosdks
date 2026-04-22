package com.huevosdks.repository;

import com.huevosdks.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// @Repository marca esta interfaz como componente de acceso a datos
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Spring genera el SQL automáticamente a partir del nombre del método
    // Busca un usuario por su número de teléfono
    Optional<Usuario> findByTelefono(String telefono);
}