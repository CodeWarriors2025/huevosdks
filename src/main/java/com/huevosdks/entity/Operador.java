package com.huevosdks.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "operadores")
public class Operador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "zona_asignada", length = 80)
    private String zonaAsignada;

    @Column(nullable = false)
    private boolean activo = true;

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public String getZonaAsignada() { return zonaAsignada; }
    public void setZonaAsignada(String zonaAsignada) { this.zonaAsignada = zonaAsignada; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
