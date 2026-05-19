package com.huevosdks.dto;

import java.util.Locale;

public class ReporteEstadoPedidoDTO {

    private String estado;
    private long cantidad;
    private double porcentaje;

    public ReporteEstadoPedidoDTO() {
    }

    public ReporteEstadoPedidoDTO(String estado, long cantidad, double porcentaje) {
        this.estado = estado;
        this.cantidad = cantidad;
        this.porcentaje = porcentaje;
    }

    public String getEstado() {
        return estado;
    }

    public long getCantidad() {
        return cantidad;
    }

    public double getPorcentaje() {
        return porcentaje;
    }

    public String getPorcentajeFormateado() {
        return String.format(Locale.US, "%.1f%%", porcentaje);
    }
}
