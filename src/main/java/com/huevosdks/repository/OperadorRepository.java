package com.huevosdks.repository;

import com.huevosdks.entity.Operador;
import com.huevosdks.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OperadorRepository extends JpaRepository<Operador, Long> {

    // Busca el operador asociado a un usuario (usado al loguear)
    Optional<Operador> findByUsuario(Usuario usuario);
}
