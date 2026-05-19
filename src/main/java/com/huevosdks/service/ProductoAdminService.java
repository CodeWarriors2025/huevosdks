package com.huevosdks.service;

import com.huevosdks.dto.ProductoAdminDTO;
import com.huevosdks.dto.ProductoAdminFormDTO;
import com.huevosdks.entity.BitacoraInventario;
import com.huevosdks.entity.Inventario;
import com.huevosdks.entity.Producto;
import com.huevosdks.repository.BitacoraInventarioRepository;
import com.huevosdks.repository.InventarioRepository;
import com.huevosdks.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ProductoAdminService {

    private final ProductoRepository productoRepository;
    private final InventarioRepository inventarioRepository;
    private final BitacoraInventarioRepository bitacoraInventarioRepository;

    public ProductoAdminService(
            ProductoRepository productoRepository,
            InventarioRepository inventarioRepository,
            BitacoraInventarioRepository bitacoraInventarioRepository
    ) {
        this.productoRepository = productoRepository;
        this.inventarioRepository = inventarioRepository;
        this.bitacoraInventarioRepository = bitacoraInventarioRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductoAdminDTO> listarProductosAdmin() {
        return productoRepository.findAllByOrderByNombreAsc()
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductoAdminFormDTO obtenerFormularioEdicion(Long productoId) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado."));

        Inventario inventario = inventarioRepository.findByProducto(producto)
                .orElse(null);

        ProductoAdminFormDTO form = new ProductoAdminFormDTO();
        form.setNombre(producto.getNombre());
        form.setDescripcion(producto.getDescripcion());
        form.setPrecioUnitario(producto.getPrecioUnitario());
        form.setTipo(producto.getTipo().name());
        form.setUnidadesPorPresentacion(producto.getUnidadesPorPresentacion());
        form.setCantidadDisponible(inventario == null ? 0 : inventario.getCantidadDisponible());
        form.setActivo(producto.isActivo());

        return form;
    }

    @Transactional
    public void crearProducto(ProductoAdminFormDTO form) {
        if (productoRepository.existsByNombreIgnoreCase(form.getNombre().trim())) {
            throw new IllegalArgumentException("Ya existe un producto con ese nombre.");
        }

        Producto producto = new Producto();
        producto.setNombre(form.getNombre().trim());
        producto.setDescripcion(form.getDescripcion());
        producto.setPrecioUnitario(form.getPrecioUnitario());
        producto.setTipo(convertirTipo(form.getTipo()));
        producto.setUnidadesPorPresentacion(form.getUnidadesPorPresentacion());
        producto.setActivo(form.isActivo());

        Producto productoGuardado = productoRepository.save(producto);

        Inventario inventario = new Inventario();
        inventario.setProducto(productoGuardado);
        inventario.setCantidadDisponible(form.getCantidadDisponible());
        inventario.setFechaActualizacion(LocalDateTime.now());

        Inventario inventarioGuardado = inventarioRepository.save(inventario);

        guardarBitacora(
                inventarioGuardado,
                0,
                form.getCantidadDisponible(),
                "Creación inicial de producto e inventario"
        );
    }

    @Transactional
    public void editarProducto(Long productoId, ProductoAdminFormDTO form) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado."));

        Optional<Producto> productoConMismoNombre = productoRepository.findByNombreIgnoreCase(form.getNombre().trim());

        if (productoConMismoNombre.isPresent()
                && !productoConMismoNombre.get().getId().equals(productoId)) {
            throw new IllegalArgumentException("Ya existe otro producto con ese nombre.");
        }

        producto.setNombre(form.getNombre().trim());
        producto.setDescripcion(form.getDescripcion());
        producto.setPrecioUnitario(form.getPrecioUnitario());
        producto.setTipo(convertirTipo(form.getTipo()));
        producto.setUnidadesPorPresentacion(form.getUnidadesPorPresentacion());
        producto.setActivo(form.isActivo());

        productoRepository.save(producto);

        Inventario inventario = inventarioRepository.findByProducto(producto)
                .orElseGet(() -> {
                    Inventario nuevoInventario = new Inventario();
                    nuevoInventario.setProducto(producto);
                    nuevoInventario.setCantidadDisponible(0);
                    nuevoInventario.setFechaActualizacion(LocalDateTime.now());
                    return inventarioRepository.save(nuevoInventario);
                });

        int cantidadAnterior = inventario.getCantidadDisponible();
        int cantidadNueva = form.getCantidadDisponible();

        if (cantidadAnterior != cantidadNueva) {
            inventario.setCantidadDisponible(cantidadNueva);
            inventario.setFechaActualizacion(LocalDateTime.now());
            inventarioRepository.save(inventario);

            guardarBitacora(
                    inventario,
                    cantidadAnterior,
                    cantidadNueva,
                    "Ajuste manual desde panel administrador"
            );
        }
    }

    @Transactional
    public void cambiarEstadoProducto(Long productoId, boolean activo) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado."));

        producto.setActivo(activo);
        productoRepository.save(producto);
    }

    private ProductoAdminDTO convertirADTO(Producto producto) {
        Integer cantidadDisponible = inventarioRepository.findByProducto(producto)
                .map(Inventario::getCantidadDisponible)
                .orElse(0);

        return new ProductoAdminDTO(
                producto.getId(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecioUnitario(),
                producto.getTipo().name(),
                producto.getUnidadesPorPresentacion(),
                cantidadDisponible,
                producto.isActivo()
        );
    }

    private Producto.TipoProducto convertirTipo(String tipo) {
        try {
            return Producto.TipoProducto.valueOf(tipo.trim().toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Tipo de producto no válido.");
        }
    }

    private void guardarBitacora(
            Inventario inventario,
            Integer cantidadAnterior,
            Integer cantidadNueva,
            String motivo
    ) {
        BitacoraInventario bitacora = new BitacoraInventario();
        bitacora.setInventario(inventario);
        bitacora.setCantidadAnterior(cantidadAnterior);
        bitacora.setCantidadNueva(cantidadNueva);
        bitacora.setMotivo(motivo);
        bitacora.setFecha(LocalDateTime.now());

        bitacoraInventarioRepository.save(bitacora);
    }
}
