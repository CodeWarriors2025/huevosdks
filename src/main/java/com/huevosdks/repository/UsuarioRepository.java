package com.huevosdks.repository;

import com.huevosdks.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByTelefono(String telefono);

    boolean existsByTelefono(String telefono);

    List<Usuario> findAllByOrderByFechaCreacionDesc();

    List<Usuario> findByRolOrderByFechaCreacionDesc(Usuario.Rol rol);

    long countByRolAndActivoTrue(Usuario.Rol rol);
}