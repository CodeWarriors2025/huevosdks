package com.huevosdks.controller;

import com.huevosdks.service.PedidoService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class OperadorPedidoController {

    private final PedidoService pedidoService;

    public OperadorPedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping("/operador/pedidos")
    public String listarPedidosOperador(
            Authentication authentication,
            Model model
    ) {
        model.addAttribute("titulo", "Pedidos en ruta");
        model.addAttribute("usuario", authentication.getName());
        model.addAttribute("pedidos", pedidoService.listarPedidosOperador());

        return "operador-pedidos";
    }

    @PostMapping("/operador/pedidos/marcar-entregado")
    public String marcarEntregado(
            @RequestParam Long pedidoId,
            RedirectAttributes redirectAttributes
    ) {
        try {
            pedidoService.marcarPedidoEntregadoOperador(pedidoId);
            redirectAttributes.addFlashAttribute("mensaje", "Pedido marcado como entregado.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/operador/pedidos";
    }
}
