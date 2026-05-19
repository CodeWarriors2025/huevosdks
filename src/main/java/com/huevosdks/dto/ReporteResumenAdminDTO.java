package com.huevosdks.dto;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class ReporteResumenAdminDTO {

    private long totalPedidos;
    private long pedidosPendientes;
    private long pedidosEnRuta;
    private long pedidosEntregados;
    private long pedidosCancelados;

    private long totalClientes;
    private long totalUsuarios;
    private long totalProductos;
    private long productosActivos;
    private long productosBajoInventario;

    private BigDecimal ventasNoCanceladas;
    private BigDecimal ventasEntregadas;

    public ReporteResumenAdminDTO() {
    }

    public ReporteResumenAdminDTO(
            long totalPedidos,
            long pedidosPendientes,
            long pedidosEnRuta,
            long pedidosEntregados,
            long pedidosCancelados,
            long totalClientes,
            long totalUsuarios,
            long totalProductos,
            long productosActivos,
            long productosBajoInventario,
            BigDecimal ventasNoCanceladas,
            BigDecimal ventasEntregadas
    ) {
        this.totalPedidos = totalPedidos;
        this.pedidosPendientes = pedidosPendientes;
        this.pedidosEnRuta = pedidosEnRuta;
        this.pedidosEntregados = pedidosEntregados;
        this.pedidosCancelados = pedidosCancelados;
        this.totalClientes = totalClientes;
        this.totalUsuarios = totalUsuarios;
        this.totalProductos = totalProductos;
        this.productosActivos = productosActivos;
        this.productosBajoInventario = productosBajoInventario;
        this.ventasNoCanceladas = ventasNoCanceladas;
        this.ventasEntregadas = ventasEntregadas;
    }

    public long getTotalPedidos() {
        return totalPedidos;
    }

    public long getPedidosPendientes() {
        return pedidosPendientes;
    }

    public long getPedidosEnRuta() {
        return pedidosEnRuta;
    }

    public long getPedidosEntregados() {
        return pedidosEntregados;
    }

    public long getPedidosCancelados() {
        return pedidosCancelados;
    }

    public long getTotalClientes() {
        return totalClientes;
    }

    public long getTotalUsuarios() {
        return totalUsuarios;
    }

    public long getTotalProductos() {
        return totalProductos;
    }

    public long getProductosActivos() {
        return productosActivos;
    }

    public long getProductosBajoInventario() {
        return productosBajoInventario;
    }

    public BigDecimal getVentasNoCanceladas() {
        return ventasNoCanceladas;
    }

    public BigDecimal getVentasEntregadas() {
        return ventasEntregadas;
    }

    public String getVentasNoCanceladasFormateadas() {
        return formatearCOP(ventasNoCanceladas);
    }

    public String getVentasEntregadasFormateadas() {
        return formatearCOP(ventasEntregadas);
    }

    private String formatearCOP(BigDecimal valor) {
        BigDecimal valorSeguro = valor == null ? BigDecimal.ZERO : valor;

        NumberFormat formato = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
        formato.setMaximumFractionDigits(0);
        return formato.format(valorSeguro);
    }
}
