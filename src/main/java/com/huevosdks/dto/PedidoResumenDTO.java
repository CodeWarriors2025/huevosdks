package com.huevosdks.dto;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class PedidoResumenDTO {

    private Long id;
    private LocalDateTime fechaPedido;
    private String estado;
    private String metodoPago;
    private String direccionEntrega;
    private String observaciones;
    private BigDecimal total;
    private List<DetallePedidoResumenDTO> detalles;

    public PedidoResumenDTO() {
    }

    public PedidoResumenDTO(
            Long id,
            LocalDateTime fechaPedido,
            String estado,
            String metodoPago,
            String direccionEntrega,
            String observaciones,
            BigDecimal total,
            List<DetallePedidoResumenDTO> detalles
    ) {
        this.id = id;
        this.fechaPedido = fechaPedido;
        this.estado = estado;
        this.metodoPago = metodoPago;
        this.direccionEntrega = direccionEntrega;
        this.observaciones = observaciones;
        this.total = total;
        this.detalles = detalles;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getFechaPedido() {
        return fechaPedido;
    }

    public String getEstado() {
        return estado;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public String getDireccionEntrega() {
        return direccionEntrega;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public List<DetallePedidoResumenDTO> getDetalles() {
        return detalles;
    }

    public String getFechaPedidoFormateada() {
        if (fechaPedido == null) {
            return "";
        }

        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return fechaPedido.format(formato);
    }

    public String getTotalFormateado() {
        if (total == null) {
            total = BigDecimal.ZERO;
        }

        NumberFormat formato = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
        formato.setMaximumFractionDigits(0);
        return formato.format(total);
    }
}