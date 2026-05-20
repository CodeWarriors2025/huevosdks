package com.huevosdks.service;

import com.huevosdks.dto.ClienteAdminDTO;
import com.huevosdks.dto.OperadorAdminFormDTO;
import com.huevosdks.dto.UsuarioAdminDTO;
import com.huevosdks.entity.Cliente;
import com.huevosdks.entity.Usuario;
import com.huevosdks.repository.ClienteRepository;
import com.huevosdks.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UsuarioAdminService {

    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioAdminService(
            UsuarioRepository usuarioRepository,
            ClienteRepository clienteRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.usuarioRepository = usuarioRepository;
        this.clienteRepository = clienteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<UsuarioAdminDTO> listarUsuarios(String rolFiltro) {
        if (rolFiltro == null || rolFiltro.isBlank()) {
            return usuarioRepository.findAllByOrderByFechaCreacionDesc()
                    .stream()
                    .map(this::convertirUsuarioADTO)
                    .toList();
        }

        try {
            Usuario.Rol rol = Usuario.Rol.valueOf(rolFiltro.trim().toUpperCase());

            return usuarioRepository.findByRolOrderByFechaCreacionDesc(rol)
                    .stream()
                    .map(this::convertirUsuarioADTO)
                    .toList();

        } catch (IllegalArgumentException e) {
            return usuarioRepository.findAllByOrderByFechaCreacionDesc()
                    .stream()
                    .map(this::convertirUsuarioADTO)
                    .toList();
        }
    }

    @Transactional(readOnly = true)
    public List<ClienteAdminDTO> listarClientes() {
        return clienteRepository.findAllByOrderByFechaRegistroDesc()
                .stream()
                .map(this::convertirClienteADTO)
                .toList();
    }

    @Transactional
    public void crearOperador(OperadorAdminFormDTO operadorForm) {
        String telefono = operadorForm.getTelefono().trim();

        if (usuarioRepository.existsByTelefono(telefono)) {
            throw new IllegalArgumentException("Ya existe un usuario con ese teléfono.");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(operadorForm.getNombre().trim());
        usuario.setTelefono(telefono);
        usuario.setContrasenia(passwordEncoder.encode(operadorForm.getContrasenia()));
        usuario.setRol(Usuario.Rol.OPERADOR);
        usuario.setActivo(operadorForm.isActivo());
        usuario.setFechaCreacion(LocalDateTime.now());

        usuarioRepository.save(usuario);
    }

    @Transactional
    public void cambiarEstadoUsuario(Long usuarioId, boolean activo, String telefonoAdminActual) {
        if (usuarioId == null) {
            throw new IllegalArgumentException("Usuario inválido.");
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

        if (!activo && usuario.getTelefono().equals(telefonoAdminActual)) {
            throw new IllegalArgumentException("No puedes desactivar tu propio usuario.");
        }

        if (!activo && usuario.getRol() == Usuario.Rol.ADMIN && usuario.isActivo()) {
            long adminsActivos = usuarioRepository.countByRolAndActivoTrue(Usuario.Rol.ADMIN);

            if (adminsActivos <= 1) {
                throw new IllegalArgumentException("No puedes desactivar el último administrador activo.");
            }
        }

        usuario.setActivo(activo);
        usuarioRepository.save(usuario);
    }

    private UsuarioAdminDTO convertirUsuarioADTO(Usuario usuario) {
        return new UsuarioAdminDTO(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getTelefono(),
                usuario.getRol().name(),
                usuario.isActivo(),
                usuario.getFechaCreacion()
        );
    }

    private ClienteAdminDTO convertirClienteADTO(Cliente cliente) {
        String usuarioTelefono = "";
        boolean usuarioActivo = false;

        if (cliente.getUsuario() != null) {
            usuarioTelefono = cliente.getUsuario().getTelefono();
            usuarioActivo = cliente.getUsuario().isActivo();
        }

        return new ClienteAdminDTO(
                cliente.getId(),
                cliente.getNombre(),
                cliente.getTelefono(),
                cliente.getDireccion(),
                cliente.getBarrio(),
                cliente.getLocalidad(),
                cliente.getFechaRegistro(),
                usuarioTelefono,
                usuarioActivo
        );
    }
}