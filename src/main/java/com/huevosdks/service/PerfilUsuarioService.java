package com.huevosdks.service;

import com.huevosdks.dto.PerfilUsuarioDTO;
import com.huevosdks.entity.Cliente;
import com.huevosdks.entity.Usuario;
import com.huevosdks.repository.ClienteRepository;
import com.huevosdks.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PerfilUsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;

    public PerfilUsuarioService(
            UsuarioRepository usuarioRepository,
            ClienteRepository clienteRepository
    ) {
        this.usuarioRepository = usuarioRepository;
        this.clienteRepository = clienteRepository;
    }

    @Transactional(readOnly = true)
    public PerfilUsuarioDTO obtenerPerfil(String telefonoUsuario) {
        Usuario usuario = usuarioRepository.findByTelefono(telefonoUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

        PerfilUsuarioDTO perfil = new PerfilUsuarioDTO();
        perfil.setNombre(usuario.getNombre());
        perfil.setTelefono(usuario.getTelefono());
        perfil.setRol(usuario.getRol().name());

        if (usuario.getRol() == Usuario.Rol.CLIENTE) {
            Cliente cliente = clienteRepository.findByUsuario(usuario);

            perfil.setCliente(true);

            if (cliente != null) {
                perfil.setNombre(cliente.getNombre());
                perfil.setDireccion(cliente.getDireccion());
                perfil.setBarrio(cliente.getBarrio());
                perfil.setLocalidad(cliente.getLocalidad());
            }
        } else {
            perfil.setCliente(false);
        }

        return perfil;
    }

    @Transactional
    public void actualizarPerfil(String telefonoUsuario, PerfilUsuarioDTO perfilActualizado) {
        Usuario usuario = usuarioRepository.findByTelefono(telefonoUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

        usuario.setNombre(perfilActualizado.getNombre().trim());
        usuarioRepository.save(usuario);

        if (usuario.getRol() == Usuario.Rol.CLIENTE) {
            Cliente cliente = clienteRepository.findByUsuario(usuario);

            if (cliente == null) {
                throw new IllegalArgumentException("No existe información de cliente para este usuario.");
            }

            cliente.setNombre(perfilActualizado.getNombre().trim());
            cliente.setDireccion(textoSeguro(perfilActualizado.getDireccion()));
            cliente.setBarrio(textoSeguro(perfilActualizado.getBarrio()));
            cliente.setLocalidad(textoSeguro(perfilActualizado.getLocalidad()));

            clienteRepository.save(cliente);
        }
    }

    private String textoSeguro(String texto) {
        if (texto == null) {
            return "";
        }

        return texto.trim();
    }
}
