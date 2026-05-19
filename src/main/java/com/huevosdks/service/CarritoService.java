package com.huevosdks.service;

import com.huevosdks.dto.CarritoDTO;
import com.huevosdks.dto.CarritoItemDTO;
import com.huevosdks.entity.Inventario;
import com.huevosdks.entity.Producto;
import com.huevosdks.repository.InventarioRepository;
import com.huevosdks.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CarritoService {

    private final ProductoRepository productoRepository;
    private final InventarioRepository inventarioRepository;

    public CarritoService(
            ProductoRepository productoRepository,
            InventarioRepository inventarioRepository
    ) {
        this.productoRepository = productoRepository;
        this.inventarioRepository = inventarioRepository;
    }

    @Transactional(readOnly = true)
    public void agregarProducto(CarritoDTO carrito, Long productoId, Integer cantidad) {
        if (productoId == null) {
            throw new IllegalArgumentException("Producto inválido");
        }

        int cantidadSolicitada = normalizarCantidad(cantidad);

        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new IllegalArgumentException("El producto no existe"));

        if (!producto.isActivo()) {
            throw new IllegalArgumentException("El producto no está disponible");
        }

        int stockDisponible = obtenerStockDisponible(producto);

        if (stockDisponible <= 0) {
            throw new IllegalArgumentException("El producto está agotado");
        }

        Optional<CarritoItemDTO> itemExistente = buscarItem(carrito, productoId);

        int cantidadActual = itemExistente
                .map(CarritoItemDTO::getCantidad)
                .orElse(0);

        int nuevaCantidad = cantidadActual + cantidadSolicitada;

        if (nuevaCantidad > stockDisponible) {
            throw new IllegalArgumentException("No hay stock suficiente. Disponible: " + stockDisponible);
        }

        if (itemExistente.isPresent()) {
            itemExistente.get().setCantidad(nuevaCantidad);
            itemExistente.get().setCantidadDisponible(stockDisponible);
        } else {
            CarritoItemDTO nuevoItem = new CarritoItemDTO(
                    producto.getId(),
                    producto.getNombre(),
                    producto.getDescripcion(),
                    producto.getPrecioUnitario(),
                    cantidadSolicitada,
                    stockDisponible
            );

            carrito.getItems().add(nuevoItem);
        }
    }

    @Transactional(readOnly = true)
    public void actualizarCantidad(CarritoDTO carrito, Long productoId, Integer cantidad) {
        if (productoId == null) {
            throw new IllegalArgumentException("Producto inválido");
        }

        int nuevaCantidad = normalizarCantidad(cantidad);

        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new IllegalArgumentException("El producto no existe"));

        int stockDisponible = obtenerStockDisponible(producto);

        if (nuevaCantidad > stockDisponible) {
            throw new IllegalArgumentException("No hay stock suficiente. Disponible: " + stockDisponible);
        }

        buscarItem(carrito, productoId).ifPresent(item -> {
            item.setCantidad(nuevaCantidad);
            item.setCantidadDisponible(stockDisponible);
        });
    }

    public void eliminarProducto(CarritoDTO carrito, Long productoId) {
        if (productoId == null || carrito == null || carrito.getItems() == null) {
            return;
        }

        carrito.getItems().removeIf(item -> productoId.equals(item.getProductoId()));
    }

    public void vaciar(CarritoDTO carrito) {
        if (carrito != null && carrito.getItems() != null) {
            carrito.getItems().clear();
        }
    }

    private Optional<CarritoItemDTO> buscarItem(CarritoDTO carrito, Long productoId) {
        return carrito.getItems().stream()
                .filter(item -> productoId.equals(item.getProductoId()))
                .findFirst();
    }

    private int obtenerStockDisponible(Producto producto) {
        return inventarioRepository.findByProducto(producto)
                .map(Inventario::getCantidadDisponible)
                .orElse(0);
    }

    private int normalizarCantidad(Integer cantidad) {
        if (cantidad == null || cantidad < 1) {
            return 1;
        }

        return cantidad;
    }
}