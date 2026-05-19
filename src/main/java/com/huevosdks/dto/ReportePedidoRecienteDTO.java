package com.huevosdks.dto;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class ReportePedidoRecienteDTO {

    private Long id;
    private LocalDateTime fechaPedido;
    private String estado;
    private String clienteNombre;
    private String clienteTelefono;
    private BigDecimal total;

    public ReportePedidoRecienteDTO() {
    }

    public ReportePedidoRecienteDTO(
            Long id,
            LocalDateTime fechaPedido,
            String estado,
            String clienteNombre,
            String clienteTelefono,
            BigDecimal total
    ) {
        this.id = id;
        this.fechaPedido = fechaPedido;
        this.estado = estado;
        this.clienteNombre = clienteNombre;
        this.clienteTelefono = clienteTelefono;
        this.total = total;
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

    public String getClienteNombre() {
        return clienteNombre;
    }

    public String getClienteTelefono() {
        return clienteTelefono;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public String getFechaPedidoFormateada() {
        if (fechaPedido == null) {
            return "";
        }

        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return fechaPedido.format(formato);
    }

    public String getTotalFormateado() {
        BigDecimal valorSeguro = total == null ? BigDecimal.ZERO : total;

        NumberFormat formato = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
        formato.setMaximumFractionDigits(0);
        return formato.format(valorSeguro);
    }
}
