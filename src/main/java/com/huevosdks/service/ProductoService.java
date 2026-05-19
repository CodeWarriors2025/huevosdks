package com.huevosdks.service;

import com.huevosdks.dto.ProductoCatalogoDTO;
import com.huevosdks.entity.Inventario;
import com.huevosdks.entity.Producto;
import com.huevosdks.repository.InventarioRepository;
import com.huevosdks.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final InventarioRepository inventarioRepository;

    public ProductoService(
            ProductoRepository productoRepository,
            InventarioRepository inventarioRepository
    ) {
        this.productoRepository = productoRepository;
        this.inventarioRepository = inventarioRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductoCatalogoDTO> listarCatalogo(String tipoTexto, String busqueda) {
        List<Producto> productos = obtenerProductosBase(tipoTexto);

        return productos.stream()
                .filter(producto -> coincideConBusqueda(producto, busqueda))
                .sorted(Comparator.comparing(Producto::getNombre, String.CASE_INSENSITIVE_ORDER))
                .map(this::convertirADTO)
                .toList();
    }

    private List<Producto> obtenerProductosBase(String tipoTexto) {
        if (tipoTexto == null || tipoTexto.isBlank()) {
            return productoRepository.findByActivoTrue();
        }

        try {
            Producto.TipoProducto tipo = Producto.TipoProducto.valueOf(tipoTexto.trim().toUpperCase());
            return productoRepository.findByActivoTrueAndTipo(tipo);
        } catch (IllegalArgumentException e) {
            return productoRepository.findByActivoTrue();
        }
    }

    private boolean coincideConBusqueda(Producto producto, String busqueda) {
        if (busqueda == null || busqueda.isBlank()) {
            return true;
        }

        String texto = busqueda.trim().toLowerCase();

        return producto.getNombre().toLowerCase().contains(texto)
                || (producto.getDescripcion() != null
                && producto.getDescripcion().toLowerCase().contains(texto));
    }

    private ProductoCatalogoDTO convertirADTO(Producto producto) {
        Integer cantidadDisponible = inventarioRepository.findByProducto(producto)
                .map(Inventario::getCantidadDisponible)
                .orElse(0);

        boolean agotado = cantidadDisponible <= 0;

        return new ProductoCatalogoDTO(
                producto.getId(),
                producto.getNombre(),
                producto.getDescripcion(),
                formatearCOP(producto.getPrecioUnitario()),
                producto.getTipo().name(),
                producto.getUnidadesPorPresentacion(),
                cantidadDisponible,
                agotado
        );
    }

    private String formatearCOP(BigDecimal valor) {
        NumberFormat formato = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
        formato.setMaximumFractionDigits(0);
        return formato.format(valor);
    }
}
