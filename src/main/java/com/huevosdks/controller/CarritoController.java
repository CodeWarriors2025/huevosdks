package com.huevosdks.controller;

import com.huevosdks.dto.CarritoDTO;
import com.huevosdks.service.CarritoService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@SessionAttributes("carrito")
public class CarritoController {

    private final CarritoService carritoService;

    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
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
}