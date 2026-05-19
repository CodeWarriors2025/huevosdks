package com.huevosdks.service;

import com.huevosdks.dto.ReporteEstadoPedidoDTO;
import com.huevosdks.dto.ReportePedidoRecienteDTO;
import com.huevosdks.dto.ReporteProductoInventarioDTO;
import com.huevosdks.dto.ReporteResumenAdminDTO;
import com.huevosdks.entity.Inventario;
import com.huevosdks.entity.Pedido;
import com.huevosdks.entity.Producto;
import com.huevosdks.repository.ClienteRepository;
import com.huevosdks.repository.InventarioRepository;
import com.huevosdks.repository.PedidoRepository;
import com.huevosdks.repository.ProductoRepository;
import com.huevosdks.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Service
public class ReporteAdminService {

    public static final int UMBRAL_INVENTARIO_BAJO = 6;

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final InventarioRepository inventarioRepository;

    public ReporteAdminService(
            PedidoRepository pedidoRepository,
            ClienteRepository clienteRepository,
            UsuarioRepository usuarioRepository,
            ProductoRepository productoRepository,
            InventarioRepository inventarioRepository
    ) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
        this.inventarioRepository = inventarioRepository;
    }

    @Transactional(readOnly = true)
    public ReporteResumenAdminDTO obtenerResumenGeneral() {
        List<Pedido> pedidos = pedidoRepository.findAllByOrderByFechaPedidoDesc();
        List<Producto> productos = productoRepository.findAllByOrderByNombreAsc();

        long pedidosPendientes = contarPedidosPorEstado(pedidos, Pedido.EstadoPedido.PENDIENTE);
        long pedidosEnRuta = contarPedidosPorEstado(pedidos, Pedido.EstadoPedido.EN_RUTA);
        long pedidosEntregados = contarPedidosPorEstado(pedidos, Pedido.EstadoPedido.ENTREGADO);
        long pedidosCancelados = contarPedidosPorEstado(pedidos, Pedido.EstadoPedido.CANCELADO);

        BigDecimal ventasNoCanceladas = pedidos.stream()
                .filter(pedido -> pedido.getEstado() != Pedido.EstadoPedido.CANCELADO)
                .map(this::obtenerTotalSeguro)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal ventasEntregadas = pedidos.stream()
                .filter(pedido -> pedido.getEstado() == Pedido.EstadoPedido.ENTREGADO)
                .map(this::obtenerTotalSeguro)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long productosActivos = productos.stream()
                .filter(Producto::isActivo)
                .count();

        long productosBajoInventario = productos.stream()
                .filter(producto -> obtenerCantidadDisponible(producto) <= UMBRAL_INVENTARIO_BAJO)
                .count();

        return new ReporteResumenAdminDTO(
                pedidos.size(),
                pedidosPendientes,
                pedidosEnRuta,
                pedidosEntregados,
                pedidosCancelados,
                clienteRepository.count(),
                usuarioRepository.count(),
                productos.size(),
                productosActivos,
                productosBajoInventario,
                ventasNoCanceladas,
                ventasEntregadas
        );
    }

    @Transactional(readOnly = true)
    public List<ReporteEstadoPedidoDTO> obtenerPedidosPorEstado() {
        List<Pedido> pedidos = pedidoRepository.findAllByOrderByFechaPedidoDesc();
        long totalPedidos = pedidos.size();

        return List.of(
                construirEstadoDTO(pedidos, totalPedidos, Pedido.EstadoPedido.PENDIENTE),
                construirEstadoDTO(pedidos, totalPedidos, Pedido.EstadoPedido.EN_RUTA),
                construirEstadoDTO(pedidos, totalPedidos, Pedido.EstadoPedido.ENTREGADO),
                construirEstadoDTO(pedidos, totalPedidos, Pedido.EstadoPedido.CANCELADO)
        );
    }

    @Transactional(readOnly = true)
    public List<ReporteProductoInventarioDTO> obtenerProductosBajoInventario() {
        return productoRepository.findAllByOrderByNombreAsc()
                .stream()
                .map(this::convertirProductoInventarioDTO)
                .filter(producto -> producto.getCantidadDisponible() <= UMBRAL_INVENTARIO_BAJO)
                .sorted(Comparator.comparing(ReporteProductoInventarioDTO::getCantidadDisponible))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReportePedidoRecienteDTO> obtenerPedidosRecientes() {
        return pedidoRepository.findAllByOrderByFechaPedidoDesc()
                .stream()
                .limit(8)
                .map(this::convertirPedidoRecienteDTO)
                .toList();
    }

    private long contarPedidosPorEstado(List<Pedido> pedidos, Pedido.EstadoPedido estado) {
        return pedidos.stream()
                .filter(pedido -> pedido.getEstado() == estado)
                .count();
    }

    private ReporteEstadoPedidoDTO construirEstadoDTO(
            List<Pedido> pedidos,
            long totalPedidos,
            Pedido.EstadoPedido estado
    ) {
        long cantidad = contarPedidosPorEstado(pedidos, estado);
        double porcentaje = totalPedidos == 0 ? 0 : (cantidad * 100.0) / totalPedidos;

        return new ReporteEstadoPedidoDTO(
                estado.name(),
                cantidad,
                porcentaje
        );
    }

    private ReporteProductoInventarioDTO convertirProductoInventarioDTO(Producto producto) {
        return new ReporteProductoInventarioDTO(
                producto.getId(),
                producto.getNombre(),
                producto.getTipo().name(),
                producto.getUnidadesPorPresentacion(),
                obtenerCantidadDisponible(producto),
                producto.isActivo()
        );
    }

    private ReportePedidoRecienteDTO convertirPedidoRecienteDTO(Pedido pedido) {
        String clienteNombre = "";
        String clienteTelefono = "";

        if (pedido.getCliente() != null) {
            clienteNombre = pedido.getCliente().getNombre();
            clienteTelefono = pedido.getCliente().getTelefono();
        }

        return new ReportePedidoRecienteDTO(
                pedido.getId(),
                pedido.getFechaPedido(),
                pedido.getEstado().name(),
                clienteNombre,
                clienteTelefono,
                pedido.getTotal()
        );
    }

    private int obtenerCantidadDisponible(Producto producto) {
        return inventarioRepository.findByProducto(producto)
                .map(Inventario::getCantidadDisponible)
                .orElse(0);
    }

    private BigDecimal obtenerTotalSeguro(Pedido pedido) {
        return pedido.getTotal() == null ? BigDecimal.ZERO : pedido.getTotal();
    }
}
