package com.huevosdks.service;

import com.huevosdks.dto.CarritoDTO;
import com.huevosdks.dto.CarritoItemDTO;
import com.huevosdks.dto.DetallePedidoResumenDTO;
import com.huevosdks.dto.PedidoListadoDTO;
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

        validarMinimoHuevosPedido(carrito);

        Usuario usuario = usuarioRepository.findByTelefono(telefonoUsuario)
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
            Producto producto = productoRepository.findById(item.getProductoId())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado."));

            Inventario inventario = inventarioRepository.findByProducto(producto)
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
        Cliente cliente = obtenerClientePorTelefonoUsuario(telefonoUsuario);

        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado."));

        if (pedido.getCliente() == null || !pedido.getCliente().getId().equals(cliente.getId())) {
            throw new IllegalArgumentException("No tienes permiso para ver este pedido.");
        }

        return convertirAResumenDTO(pedido);
    }

    @Transactional(readOnly = true)
    public PedidoResumenDTO obtenerResumenPedidoAdmin(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado."));

        return convertirAResumenDTO(pedido);
    }

    @Transactional(readOnly = true)
    public List<PedidoListadoDTO> listarPedidosCliente(String telefonoUsuario) {
        Cliente cliente = obtenerClientePorTelefonoUsuario(telefonoUsuario);

        return pedidoRepository.findByClienteOrderByFechaPedidoDesc(cliente)
                .stream()
                .map(this::convertirAListadoDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PedidoListadoDTO> listarPedidosAdmin(String estadoFiltro) {
        if (estadoFiltro == null || estadoFiltro.isBlank()) {
            return pedidoRepository.findAllByOrderByFechaPedidoDesc()
                    .stream()
                    .map(this::convertirAListadoDTO)
                    .toList();
        }

        try {
            Pedido.EstadoPedido estado = Pedido.EstadoPedido.valueOf(estadoFiltro.trim().toUpperCase());

            return pedidoRepository.findByEstadoOrderByFechaPedidoDesc(estado)
                    .stream()
                    .map(this::convertirAListadoDTO)
                    .toList();

        } catch (IllegalArgumentException e) {
            return pedidoRepository.findAllByOrderByFechaPedidoDesc()
                    .stream()
                    .map(this::convertirAListadoDTO)
                    .toList();
        }
    }

    @Transactional(readOnly = true)
    public List<PedidoListadoDTO> listarPedidosOperador() {
        return pedidoRepository.findByEstadoOrderByFechaPedidoDesc(Pedido.EstadoPedido.EN_RUTA)
                .stream()
                .map(this::convertirAListadoDTO)
                .toList();
    }

    @Transactional
    public void cambiarEstadoPedido(Long pedidoId, String estadoNuevo) {
        if (pedidoId == null) {
            throw new IllegalArgumentException("Pedido inválido.");
        }

        if (estadoNuevo == null || estadoNuevo.isBlank()) {
            throw new IllegalArgumentException("Debe seleccionar un estado.");
        }

        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado."));

        try {
            Pedido.EstadoPedido nuevoEstado = Pedido.EstadoPedido.valueOf(estadoNuevo.trim().toUpperCase());
            pedido.setEstado(nuevoEstado);
            pedidoRepository.save(pedido);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estado de pedido no válido.");
        }
    }

    @Transactional
    public void marcarPedidoEntregadoOperador(Long pedidoId) {
        if (pedidoId == null) {
            throw new IllegalArgumentException("Pedido inválido.");
        }

        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado."));

        if (pedido.getEstado() != Pedido.EstadoPedido.EN_RUTA) {
            throw new IllegalArgumentException("Solo se pueden entregar pedidos que estén EN_RUTA.");
        }

        pedido.setEstado(Pedido.EstadoPedido.ENTREGADO);
        pedidoRepository.save(pedido);
    }

    private void validarMinimoHuevosPedido(CarritoDTO carrito) {
        int totalHuevos = carrito.getTotalHuevos();

        if (totalHuevos < CarritoDTO.MINIMO_HUEVOS_PEDIDO) {
            throw new IllegalArgumentException(
                    "El pedido mínimo es de 6 huevos. Actualmente tienes "
                            + totalHuevos
                            + " huevo(s). Agrega "
                            + carrito.getHuevosFaltantes()
                            + " huevo(s) más."
            );
        }
    }

    private PedidoListadoDTO convertirAListadoDTO(Pedido pedido) {
        int cantidadItems = detallePedidoRepository.findByPedido(pedido)
                .stream()
                .mapToInt(detalle -> detalle.getCantidad() == null ? 0 : detalle.getCantidad())
                .sum();

        String clienteNombre = "";
        String clienteTelefono = "";

        if (pedido.getCliente() != null) {
            clienteNombre = pedido.getCliente().getNombre();
            clienteTelefono = pedido.getCliente().getTelefono();
        }

        return new PedidoListadoDTO(
                pedido.getId(),
                pedido.getFechaPedido(),
                pedido.getEstado().name(),
                pedido.getMetodoPago().name(),
                pedido.getDireccionEntrega(),
                pedido.getTotal(),
                cantidadItems,
                clienteNombre,
                clienteTelefono
        );
    }

    private PedidoResumenDTO convertirAResumenDTO(Pedido pedido) {
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

    private Cliente obtenerClientePorTelefonoUsuario(String telefonoUsuario) {
        Usuario usuario = usuarioRepository.findByTelefono(telefonoUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

        Cliente cliente = clienteRepository.findByUsuario(usuario);

        if (cliente == null) {
            throw new IllegalArgumentException("No existe información de cliente para este usuario.");
        }

        return cliente;
    }

    private void validarStockDisponible(CarritoDTO carrito) {
        for (CarritoItemDTO item : carrito.getItems()) {
            Producto producto = productoRepository.findById(item.getProductoId())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado."));

            if (!producto.isActivo()) {
                throw new IllegalArgumentException("El producto no está activo: " + producto.getNombre());
            }

            Inventario inventario = inventarioRepository.findByProducto(producto)
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
