package com.huevosdks.repository;

import com.huevosdks.entity.Cliente;
import com.huevosdks.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Cliente findByUsuario(Usuario usuario);

    boolean existsByUsuario(Usuario usuario);

    List<Cliente> findAllByOrderByFechaRegistroDesc();
}