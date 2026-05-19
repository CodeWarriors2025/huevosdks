package com.huevosdks.controller;

import com.huevosdks.entity.Pedido;
import com.huevosdks.service.PedidoService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminPedidoController {

    private final PedidoService pedidoService;

    public AdminPedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping("/admin/pedidos")
    public String listarPedidos(
            @RequestParam(required = false) String estado,
            Authentication authentication,
            Model model
    ) {
        model.addAttribute("titulo", "Gestión de pedidos");
        model.addAttribute("usuario", authentication.getName());
        model.addAttribute("pedidos", pedidoService.listarPedidosAdmin(estado));
        model.addAttribute("estados", Pedido.EstadoPedido.values());
        model.addAttribute("estadoSeleccionado", estado);

        return "admin-pedidos";
    }

    @PostMapping("/admin/pedidos/cambiar-estado")
    public String cambiarEstado(
            @RequestParam Long pedidoId,
            @RequestParam String estado,
            RedirectAttributes redirectAttributes
    ) {
        try {
            pedidoService.cambiarEstadoPedido(pedidoId, estado);
            redirectAttributes.addFlashAttribute("mensaje", "Estado del pedido actualizado correctamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/pedidos";
    }
}
