package com.huevosdks.dto;

public class ProductoCatalogoDTO {

    private Long id;
    private String nombre;
    private String descripcion;
    private String precioFormateado;
    private String tipo;
    private Integer unidadesPorPresentacion;
    private Integer cantidadDisponible;
    private boolean agotado;

    public ProductoCatalogoDTO() {
    }

    public ProductoCatalogoDTO(
            Long id,
            String nombre,
            String descripcion,
            String precioFormateado,
            String tipo,
            Integer unidadesPorPresentacion,
            Integer cantidadDisponible,
            boolean agotado
    ) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precioFormateado = precioFormateado;
        this.tipo = tipo;
        this.unidadesPorPresentacion = unidadesPorPresentacion;
        this.cantidadDisponible = cantidadDisponible;
        this.agotado = agotado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getPrecioFormateado() {
        return precioFormateado;
    }

    public void setPrecioFormateado(String precioFormateado) {
        this.precioFormateado = precioFormateado;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Integer getUnidadesPorPresentacion() {
        return unidadesPorPresentacion;
    }

    public void setUnidadesPorPresentacion(Integer unidadesPorPresentacion) {
        this.unidadesPorPresentacion = unidadesPorPresentacion;
    }

    public Integer getCantidadDisponible() {
        return cantidadDisponible;
    }

    public void setCantidadDisponible(Integer cantidadDisponible) {
        this.cantidadDisponible = cantidadDisponible;
    }

    public boolean isAgotado() {
        return agotado;
    }

    public void setAgotado(boolean agotado) {
        this.agotado = agotado;
    }
}
