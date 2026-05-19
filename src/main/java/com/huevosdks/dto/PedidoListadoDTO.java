package com.huevosdks.dto;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class PedidoListadoDTO {

    private Long id;
    private LocalDateTime fechaPedido;
    private String estado;
    private String metodoPago;
    private String direccionEntrega;
    private BigDecimal total;
    private Integer cantidadItems;
    private String clienteNombre;
    private String clienteTelefono;

    public PedidoListadoDTO() {
    }

    public PedidoListadoDTO(
            Long id,
            LocalDateTime fechaPedido,
            String estado,
            String metodoPago,
            String direccionEntrega,
            BigDecimal total,
            Integer cantidadItems,
            String clienteNombre,
            String clienteTelefono
    ) {
        this.id = id;
        this.fechaPedido = fechaPedido;
        this.estado = estado;
        this.metodoPago = metodoPago;
        this.direccionEntrega = direccionEntrega;
        this.total = total;
        this.cantidadItems = cantidadItems;
        this.clienteNombre = clienteNombre;
        this.clienteTelefono = clienteTelefono;
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

    public BigDecimal getTotal() {
        return total;
    }

    public Integer getCantidadItems() {
        return cantidadItems;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public String getClienteTelefono() {
        return clienteTelefono;
    }

    public String getFechaPedidoFormateada() {
        if (fechaPedido == null) {
            return "";
        }

        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return fechaPedido.format(formato);
    }

    public String getTotalFormateado() {
        BigDecimal valor = total == null ? BigDecimal.ZERO : total;

        NumberFormat formato = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
        formato.setMaximumFractionDigits(0);
        return formato.format(valor);
    }
}