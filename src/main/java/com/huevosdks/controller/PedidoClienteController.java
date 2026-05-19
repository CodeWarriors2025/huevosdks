package com.huevosdks.controller;

import com.huevosdks.dto.PedidoResumenDTO;
import com.huevosdks.service.PedidoService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PedidoClienteController {

    private final PedidoService pedidoService;

    public PedidoClienteController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping("/mis-pedidos")
    public String listarMisPedidos(
            Authentication authentication,
            Model model
    ) {
        model.addAttribute("titulo", "Mis pedidos");
        model.addAttribute("usuario", authentication.getName());
        model.addAttribute("pedidos", pedidoService.listarPedidosCliente(authentication.getName()));

        return "mis-pedidos";
    }

    @GetMapping("/mis-pedidos/{pedidoId}")
    public String verDetallePedido(
            @PathVariable Long pedidoId,
            Authentication authentication,
            Model model
    ) {
        PedidoResumenDTO pedido = pedidoService.obtenerResumenPedido(pedidoId, authentication.getName());

        model.addAttribute("titulo", "Detalle del pedido");
        model.addAttribute("usuario", authentication.getName());
        model.addAttribute("pedido", pedido);

        return "pedido-confirmado";
    }
}
