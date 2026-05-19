package com.huevosdks.dto;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CarritoDTO {

    public static final int MINIMO_HUEVOS_PEDIDO = 6;

    private List<CarritoItemDTO> items = new ArrayList<>();

    public List<CarritoItemDTO> getItems() {
        return items;
    }

    public void setItems(List<CarritoItemDTO> items) {
        this.items = items;
    }

    public boolean isVacio() {
        return items == null || items.isEmpty();
    }

    public int getTotalItems() {
        if (items == null) {
            return 0;
        }

        return items.stream()
                .mapToInt(item -> item.getCantidad() == null ? 0 : item.getCantidad())
                .sum();
    }

    public int getTotalHuevos() {
        if (items == null) {
            return 0;
        }

        return items.stream()
                .mapToInt(item -> item.getHuevosItem() == null ? 0 : item.getHuevosItem())
                .sum();
    }

    public boolean isCumpleMinimoPedido() {
        return getTotalHuevos() >= MINIMO_HUEVOS_PEDIDO;
    }

    public int getHuevosFaltantes() {
        int faltantes = MINIMO_HUEVOS_PEDIDO - getTotalHuevos();
        return Math.max(faltantes, 0);
    }

    public BigDecimal getTotal() {
        if (items == null) {
            return BigDecimal.ZERO;
        }

        return items.stream()
                .map(CarritoItemDTO::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public String getTotalFormateado() {
        NumberFormat formato = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
        formato.setMaximumFractionDigits(0);
        return formato.format(getTotal());
    }
}