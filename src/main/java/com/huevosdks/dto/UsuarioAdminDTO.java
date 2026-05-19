package com.huevosdks.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UsuarioAdminDTO {

    private Long id;
    private String nombre;
    private String telefono;
    private String rol;
    private boolean activo;
    private LocalDateTime fechaCreacion;

    public UsuarioAdminDTO() {
    }

    public UsuarioAdminDTO(
            Long id,
            String nombre,
            String telefono,
            String rol,
            boolean activo,
            LocalDateTime fechaCreacion
    ) {
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
        this.rol = rol;
        this.activo = activo;
        this.fechaCreacion = fechaCreacion;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getRol() {
        return rol;
    }

    public boolean isActivo() {
        return activo;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public String getFechaCreacionFormateada() {
        if (fechaCreacion == null) {
            return "";
        }

        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return fechaCreacion.format(formato);
    }

    public String getEstadoTexto() {
        return activo ? "Activo" : "Inactivo";
    }
}
