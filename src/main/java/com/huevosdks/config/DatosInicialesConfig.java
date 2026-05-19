package com.huevosdks.config;

import com.huevosdks.entity.Inventario;
import com.huevosdks.entity.Operador;
import com.huevosdks.entity.Producto;
import com.huevosdks.entity.Usuario;
import com.huevosdks.repository.InventarioRepository;
import com.huevosdks.repository.OperadorRepository;
import com.huevosdks.repository.ProductoRepository;
import com.huevosdks.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@Configuration
public class DatosInicialesConfig {

    @Bean
    CommandLineRunner cargarDatosIniciales(
            UsuarioRepository usuarioRepository,
            OperadorRepository operadorRepository,
            ProductoRepository productoRepository,
            InventarioRepository inventarioRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {

            if (!usuarioRepository.existsByTelefono("3000000001")) {
                Usuario admin = new Usuario();
                admin.setNombre("Administrador HUEVOS DKS");
                admin.setTelefono("3000000001");
                admin.setContrasenia(passwordEncoder.encode("123456"));
                admin.setRol(Usuario.Rol.ADMIN);
                admin.setActivo(true);
                usuarioRepository.save(admin);
            }

            if (!usuarioRepository.existsByTelefono("3000000002")) {
                Usuario usuarioOperador = new Usuario();
                usuarioOperador.setNombre("Operador HUEVOS DKS");
                usuarioOperador.setTelefono("3000000002");
                usuarioOperador.setContrasenia(passwordEncoder.encode("123456"));
                usuarioOperador.setRol(Usuario.Rol.OPERADOR);
                usuarioOperador.setActivo(true);
                usuarioRepository.save(usuarioOperador);

                Operador operador = new Operador();
                operador.setUsuario(usuarioOperador);
                operador.setZonaAsignada("Bosa - Kennedy");
                operador.setActivo(true);
                operadorRepository.save(operador);
            }

            if (productoRepository.count() == 0) {
                crearProductoConInventario(
                        productoRepository,
                        inventarioRepository,
                        "Cubeta huevos AA",
                        "Cubeta de 30 huevos tipo AA",
                        new BigDecimal("18000"),
                        Producto.TipoProducto.CUBETA,
                        30,
                        40
                );

                crearProductoConInventario(
                        productoRepository,
                        inventarioRepository,
                        "Panal huevos A",
                        "Panal de 30 huevos tipo A",
                        new BigDecimal("16000"),
                        Producto.TipoProducto.PANAL,
                        30,
                        35
                );

                crearProductoConInventario(
                        productoRepository,
                        inventarioRepository,
                        "Huevo por unidad",
                        "Venta individual de huevo",
                        new BigDecimal("600"),
                        Producto.TipoProducto.UNIDAD,
                        1,
                        200
                );
            }
        };
    }

    private void crearProductoConInventario(
            ProductoRepository productoRepository,
            InventarioRepository inventarioRepository,
            String nombre,
            String descripcion,
            BigDecimal precio,
            Producto.TipoProducto tipo,
            Integer unidadesPorPresentacion,
            Integer cantidadDisponible
    ) {
        Producto producto = new Producto();
        producto.setNombre(nombre);
        producto.setDescripcion(descripcion);
        producto.setPrecioUnitario(precio);
        producto.setTipo(tipo);
        producto.setUnidadesPorPresentacion(unidadesPorPresentacion);
        producto.setActivo(true);

        Producto productoGuardado = productoRepository.save(producto);

        Inventario inventario = new Inventario();
        inventario.setProducto(productoGuardado);
        inventario.setCantidadDisponible(cantidadDisponible);

        inventarioRepository.save(inventario);
    }
}
