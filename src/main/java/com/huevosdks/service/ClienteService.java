package com.huevosdks.service;

import com.huevosdks.dto.RegistroClienteDTO;
import com.huevosdks.entity.Cliente;
import com.huevosdks.entity.Usuario;
import com.huevosdks.repository.ClienteRepository;
import com.huevosdks.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClienteService {

    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;

    public ClienteService(UsuarioRepository usuarioRepository,
                          ClienteRepository clienteRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.clienteRepository = clienteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void registrar(RegistroClienteDTO dto) {

        // 1. Verificar que las contraseñas coincidan
        if (!dto.getPassword().equals(dto.getConfirmarPassword())) {
            throw new IllegalArgumentException("Las contraseñas no coinciden");
        }

        // 2. Verificar que el teléfono no esté ya registrado
        if (usuarioRepository.existsByTelefono(dto.getTelefono())) {
            throw new IllegalArgumentException("El teléfono ya está registrado");
        }

        // 3. Crear el Usuario (credenciales de login)
        Usuario usuario = new Usuario();
        usuario.setTelefono(dto.getTelefono());
        usuario.setNombre(dto.getNombre());
        usuario.setContrasenia(passwordEncoder.encode(dto.getPassword()));
        usuario.setRol(Usuario.Rol.CLIENTE);
        usuario.setActivo(true);
        usuarioRepository.save(usuario);

        // 4. Crear el Cliente vinculado al Usuario (datos de entrega)
        Cliente cliente = new Cliente();
        cliente.setNombre(dto.getNombre());
        cliente.setTelefono(dto.getTelefono());
        cliente.setDireccion(dto.getDireccion());
        cliente.setBarrio(dto.getBarrio());
        cliente.setUsuario(usuario);
        clienteRepository.save(cliente);
    }
}