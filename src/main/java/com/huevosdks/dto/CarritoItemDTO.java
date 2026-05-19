package com.huevosdks.dto;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class CarritoItemDTO {

    private Long productoId;
    private String nombre;
    private String descripcion;
    private BigDecimal precioUnitario;
    private Integer cantidad;
    private Integer cantidadDisponible;
    private Integer unidadesPorPresentacion;

    public CarritoItemDTO() {
    }

    public CarritoItemDTO(
            Long productoId,
            String nombre,
            String descripcion,
            BigDecimal precioUnitario,
            Integer cantidad,
            Integer cantidadDisponible,
            Integer unidadesPorPresentacion
    ) {
        this.productoId = productoId;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precioUnitario = precioUnitario;
        this.cantidad = cantidad;
        this.cantidadDisponible = cantidadDisponible;
        this.unidadesPorPresentacion = unidadesPorPresentacion;
    }

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
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

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Integer getCantidadDisponible() {
        return cantidadDisponible;
    }

    public void setCantidadDisponible(Integer cantidadDisponible) {
        this.cantidadDisponible = cantidadDisponible;
    }

    public Integer getUnidadesPorPresentacion() {
        return unidadesPorPresentacion;
    }

    public void setUnidadesPorPresentacion(Integer unidadesPorPresentacion) {
        this.unidadesPorPresentacion = unidadesPorPresentacion;
    }

    public Integer getHuevosItem() {
        int cantidadSegura = cantidad == null ? 0 : cantidad;
        int unidadesSeguras = unidadesPorPresentacion == null ? 1 : unidadesPorPresentacion;

        return cantidadSegura * unidadesSeguras;
    }

    public BigDecimal getSubtotal() {
        if (precioUnitario == null || cantidad == null) {
            return BigDecimal.ZERO;
        }

        return precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }

    public String getPrecioFormateado() {
        return formatearCOP(precioUnitario);
    }

    public String getSubtotalFormateado() {
        return formatearCOP(getSubtotal());
    }

    private String formatearCOP(BigDecimal valor) {
        if (valor == null) {
            valor = BigDecimal.ZERO;
        }

        NumberFormat formato = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
        formato.setMaximumFractionDigits(0);
        return formato.format(valor);
    }
}