package com.huevosdks.controller;

import com.huevosdks.dto.CarritoDTO;
import com.huevosdks.dto.PedidoResumenDTO;
import com.huevosdks.service.CarritoService;
import com.huevosdks.service.PedidoService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@SessionAttributes("carrito")
public class CarritoController {

    private final CarritoService carritoService;
    private final PedidoService pedidoService;

    public CarritoController(
            CarritoService carritoService,
            PedidoService pedidoService
    ) {
        this.carritoService = carritoService;
        this.pedidoService = pedidoService;
    }

    @ModelAttribute("carrito")
    public CarritoDTO carrito() {
        return new CarritoDTO();
    }

    @GetMapping("/carrito")
    public String verCarrito(
            @ModelAttribute("carrito") CarritoDTO carrito,
            Authentication authentication,
            Model model
    ) {
        model.addAttribute("titulo", "Mi carrito");
        model.addAttribute("usuario", authentication.getName());
        model.addAttribute("carrito", carrito);
        return "carrito";
    }

    @PostMapping("/carrito/agregar")
    public String agregarProducto(
            @ModelAttribute("carrito") CarritoDTO carrito,
            @RequestParam Long productoId,
            @RequestParam(defaultValue = "1") Integer cantidad,
            RedirectAttributes redirectAttributes
    ) {
        try {
            carritoService.agregarProducto(carrito, productoId, cantidad);
            redirectAttributes.addFlashAttribute("mensaje", "Producto agregado al carrito.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/catalogo";
    }

    @PostMapping("/carrito/actualizar")
    public String actualizarCantidad(
            @ModelAttribute("carrito") CarritoDTO carrito,
            @RequestParam Long productoId,
            @RequestParam Integer cantidad,
            RedirectAttributes redirectAttributes
    ) {
        try {
            carritoService.actualizarCantidad(carrito, productoId, cantidad);
            redirectAttributes.addFlashAttribute("mensaje", "Cantidad actualizada.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/carrito";
    }

    @PostMapping("/carrito/eliminar")
    public String eliminarProducto(
            @ModelAttribute("carrito") CarritoDTO carrito,
            @RequestParam Long productoId,
            RedirectAttributes redirectAttributes
    ) {
        carritoService.eliminarProducto(carrito, productoId);
        redirectAttributes.addFlashAttribute("mensaje", "Producto eliminado del carrito.");
        return "redirect:/carrito";
    }

    @PostMapping("/carrito/vaciar")
    public String vaciarCarrito(
            @ModelAttribute("carrito") CarritoDTO carrito,
            RedirectAttributes redirectAttributes
    ) {
        carritoService.vaciar(carrito);
        redirectAttributes.addFlashAttribute("mensaje", "Carrito vaciado.");
        return "redirect:/carrito";
    }

    @PostMapping("/carrito/crear-pedido")
    public String crearPedido(
            @ModelAttribute("carrito") CarritoDTO carrito,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Long pedidoId = pedidoService.crearPedidoDesdeCarrito(carrito, authentication.getName());
            carritoService.vaciar(carrito);
            return "redirect:/carrito/pedido-confirmado/" + pedidoId;
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/carrito";
        }
    }

    @GetMapping("/carrito/pedido-confirmado/{pedidoId}")
    public String pedidoConfirmado(
            @PathVariable Long pedidoId,
            Authentication authentication,
            Model model
    ) {
        PedidoResumenDTO pedido = pedidoService.obtenerResumenPedido(pedidoId, authentication.getName());

        model.addAttribute("titulo", "Pedido confirmado");
        model.addAttribute("usuario", authentication.getName());
        model.addAttribute("pedido", pedido);

        return "pedido-confirmado";
    }
}