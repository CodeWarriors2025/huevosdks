package com.huevosdks.dto;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class ProductoAdminDTO {

    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal precioUnitario;
    private String tipo;
    private Integer unidadesPorPresentacion;
    private Integer cantidadDisponible;
    private boolean activo;

    public ProductoAdminDTO() {
    }

    public ProductoAdminDTO(
            Long id,
            String nombre,
            String descripcion,
            BigDecimal precioUnitario,
            String tipo,
            Integer unidadesPorPresentacion,
            Integer cantidadDisponible,
            boolean activo
    ) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precioUnitario = precioUnitario;
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

    public String getDescripcion() {
        return descripcion;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
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

    public String getPrecioFormateado() {
        BigDecimal valor = precioUnitario == null ? BigDecimal.ZERO : precioUnitario;

        NumberFormat formato = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
        formato.setMaximumFractionDigits(0);
        return formato.format(valor);
    }

    public String getEstadoTexto() {
        return activo ? "Activo" : "Inactivo";
    }
}
