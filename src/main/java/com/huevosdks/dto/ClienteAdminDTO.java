package com.huevosdks.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ClienteAdminDTO {

    private Long id;
    private String nombre;
    private String telefono;
    private String direccion;
    private String barrio;
    private String localidad;
    private LocalDateTime fechaRegistro;
    private String usuarioTelefono;
    private boolean usuarioActivo;

    public ClienteAdminDTO() {
    }

    public ClienteAdminDTO(
            Long id,
            String nombre,
            String telefono,
            String direccion,
            String barrio,
            String localidad,
            LocalDateTime fechaRegistro,
            String usuarioTelefono,
            boolean usuarioActivo
    ) {
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
        this.direccion = direccion;
        this.barrio = barrio;
        this.localidad = localidad;
        this.fechaRegistro = fechaRegistro;
        this.usuarioTelefono = usuarioTelefono;
        this.usuarioActivo = usuarioActivo;
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

    public String getDireccion() {
        return direccion;
    }

    public String getBarrio() {
        return barrio;
    }

    public String getLocalidad() {
        return localidad;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public String getUsuarioTelefono() {
        return usuarioTelefono;
    }

    public boolean isUsuarioActivo() {
        return usuarioActivo;
    }

    public String getFechaRegistroFormateada() {
        if (fechaRegistro == null) {
            return "";
        }

        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return fechaRegistro.format(formato);
    }

    public String getDireccionCompleta() {
        String direccionSegura = direccion == null ? "" : direccion;
        String barrioSeguro = barrio == null ? "" : barrio;
        String localidadSegura = localidad == null ? "" : localidad;

        return direccionSegura + " - " + barrioSeguro + " - " + localidadSegura;
    }

    public String getEstadoUsuarioTexto() {
        return usuarioActivo ? "Activo" : "Inactivo";
    }
}
