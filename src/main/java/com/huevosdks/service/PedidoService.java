package com.huevosdks.service;

import com.huevosdks.dto.CarritoDTO;
import com.huevosdks.dto.CarritoItemDTO;
import com.huevosdks.dto.DetallePedidoResumenDTO;
import com.huevosdks.dto.PedidoResumenDTO;
import com.huevosdks.entity.Cliente;
import com.huevosdks.entity.DetallePedido;
import com.huevosdks.entity.Inventario;
import com.huevosdks.entity.Pedido;
import com.huevosdks.entity.Producto;
import com.huevosdks.entity.Usuario;
import com.huevosdks.repository.ClienteRepository;
import com.huevosdks.repository.DetallePedidoRepository;
import com.huevosdks.repository.InventarioRepository;
import com.huevosdks.repository.PedidoRepository;
import com.huevosdks.repository.ProductoRepository;
import com.huevosdks.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PedidoService {

    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final InventarioRepository inventarioRepository;
    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;

    public PedidoService(
            UsuarioRepository usuarioRepository,
            ClienteRepository clienteRepository,
            ProductoRepository productoRepository,
            InventarioRepository inventarioRepository,
            PedidoRepository pedidoRepository,
            DetallePedidoRepository detallePedidoRepository
    ) {
        this.usuarioRepository = usuarioRepository;
        this.clienteRepository = clienteRepository;
        this.productoRepository = productoRepository;
        this.inventarioRepository = inventarioRepository;
        this.pedidoRepository = pedidoRepository;
        this.detallePedidoRepository = detallePedidoRepository;
    }

    @Transactional
    public Long crearPedidoDesdeCarrito(CarritoDTO carrito, String telefonoUsuario) {
        if (carrito == null || carrito.isVacio()) {
            throw new IllegalArgumentException("El carrito está vacío.");
        }

        Usuario usuario = (Usuario) usuarioRepository.findByTelefono(telefonoUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

        Cliente cliente = clienteRepository.findByUsuario(usuario);

        if (cliente == null) {
            throw new IllegalArgumentException("No existe información de cliente para este usuario.");
        }

        validarStockDisponible(carrito);

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setFechaPedido(LocalDateTime.now());
        pedido.setEstado(Pedido.EstadoPedido.PENDIENTE);
        pedido.setMetodoPago(Pedido.MetodoPago.EFECTIVO);
        pedido.setDireccionEntrega(construirDireccionEntrega(cliente));
        pedido.setObservaciones("Pedido creado desde el carrito web.");
        pedido.setTotal(carrito.getTotal());

        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        for (CarritoItemDTO item : carrito.getItems()) {
            Producto producto = (Producto) productoRepository.findById(item.getProductoId())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado."));

            Inventario inventario = (Inventario) inventarioRepository.findByProducto(producto)
                    .orElseThrow(() -> new IllegalArgumentException("No existe inventario para el producto: " + producto.getNombre()));

            int cantidadAnterior = inventario.getCantidadDisponible();
            int cantidadNueva = cantidadAnterior - item.getCantidad();

            if (cantidadNueva < 0) {
                throw new IllegalArgumentException("No hay stock suficiente para el producto: " + producto.getNombre());
            }

            inventario.setCantidadDisponible(cantidadNueva);
            inventario.setFechaActualizacion(LocalDateTime.now());
            inventarioRepository.save(inventario);

            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(pedidoGuardado);
            detalle.setProducto(producto);
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(item.getPrecioUnitario());
            detalle.setSubtotal(item.getSubtotal());

            detallePedidoRepository.save(detalle);
        }

        return pedidoGuardado.getId();
    }

    @Transactional(readOnly = true)
    public PedidoResumenDTO obtenerResumenPedido(Long pedidoId, String telefonoUsuario) {
        Usuario usuario = (Usuario) usuarioRepository.findByTelefono(telefonoUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

        Cliente cliente = clienteRepository.findByUsuario(usuario);

        if (cliente == null) {
            throw new IllegalArgumentException("No existe información de cliente para este usuario.");
        }

        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado."));

        if (pedido.getCliente() == null || !pedido.getCliente().getId().equals(cliente.getId())) {
            throw new IllegalArgumentException("No tienes permiso para ver este pedido.");
        }

        List<DetallePedidoResumenDTO> detalles = detallePedidoRepository.findByPedido(pedido)
                .stream()
                .map(detalle -> new DetallePedidoResumenDTO(
                        detalle.getProducto().getNombre(),
                        detalle.getCantidad(),
                        detalle.getPrecioUnitario(),
                        detalle.getSubtotal()
                ))
                .toList();

        return new PedidoResumenDTO(
                pedido.getId(),
                pedido.getFechaPedido(),
                pedido.getEstado().name(),
                pedido.getMetodoPago().name(),
                pedido.getDireccionEntrega(),
                pedido.getObservaciones(),
                pedido.getTotal(),
                detalles
        );
    }

    private void validarStockDisponible(CarritoDTO carrito) {
        for (CarritoItemDTO item : carrito.getItems()) {
            Producto producto = (Producto) productoRepository.findById(item.getProductoId())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado."));

            if (!producto.isActivo()) {
                throw new IllegalArgumentException("El producto no está activo: " + producto.getNombre());
            }

            Inventario inventario = (Inventario) inventarioRepository.findByProducto(producto)
                    .orElseThrow(() -> new IllegalArgumentException("No existe inventario para el producto: " + producto.getNombre()));

            if (inventario.getCantidadDisponible() < item.getCantidad()) {
                throw new IllegalArgumentException("No hay stock suficiente para el producto: " + producto.getNombre());
            }
        }
    }

    private String construirDireccionEntrega(Cliente cliente) {
        String direccion = cliente.getDireccion() == null ? "" : cliente.getDireccion();
        String barrio = cliente.getBarrio() == null ? "" : cliente.getBarrio();
        String localidad = cliente.getLocalidad() == null ? "" : cliente.getLocalidad();

        return direccion + " - " + barrio + " - " + localidad;
    }
}
