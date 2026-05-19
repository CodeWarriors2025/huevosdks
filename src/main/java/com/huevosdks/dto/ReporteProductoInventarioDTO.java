package com.huevosdks.dto;

public class ReporteProductoInventarioDTO {

    private Long id;
    private String nombre;
    private String tipo;
    private Integer unidadesPorPresentacion;
    private Integer cantidadDisponible;
    private boolean activo;

    public ReporteProductoInventarioDTO() {
    }

    public ReporteProductoInventarioDTO(
            Long id,
            String nombre,
            String tipo,
            Integer unidadesPorPresentacion,
            Integer cantidadDisponible,
            boolean activo
    ) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.unidadesPorPresentacion = unidadesPorPresentacion;
        this.cantidadDisponible = cantidadDisponible;
        this.activo = activo;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public Integer getUnidadesPorPresentacion() {
        return unidadesPorPresentacion;
    }

    public Integer getCantidadDisponible() {
        return cantidadDisponible;
    }

    public boolean isActivo() {
        return activo;
    }

    public String getEstadoInventario() {
        int cantidad = cantidadDisponible == null ? 0 : cantidadDisponible;

        if (cantidad <= 0) {
            return "Agotado";
        }

        if (cantidad <= 6) {
            return "Bajo";
        }

        return "Normal";
    }
}
