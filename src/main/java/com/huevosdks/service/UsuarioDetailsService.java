package com.huevosdks.service;

import com.huevosdks.entity.Usuario;
import com.huevosdks.repository.UsuarioRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String telefono) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByTelefono(telefono)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado: " + telefono));

        String rol = "ROLE_" + usuario.getRol().name();

        return User.builder()
                .username(usuario.getTelefono())
                .password(usuario.getContrasenia())
                .authorities(new SimpleGrantedAuthority(rol))
                .disabled(!usuario.isActivo())
                .build();
    }
}