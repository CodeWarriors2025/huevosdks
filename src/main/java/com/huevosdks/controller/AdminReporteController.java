package com.huevosdks.controller;

import com.huevosdks.service.ReporteAdminService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminReporteController {

    private final ReporteAdminService reporteAdminService;

    public AdminReporteController(ReporteAdminService reporteAdminService) {
        this.reporteAdminService = reporteAdminService;
    }

    @GetMapping("/admin/reportes")
    public String verReportes(
            Authentication authentication,
            Model model
    ) {
        model.addAttribute("titulo", "Reportes administrativos");
        model.addAttribute("usuario", authentication.getName());
        model.addAttribute("resumen", reporteAdminService.obtenerResumenGeneral());
        model.addAttribute("pedidosPorEstado", reporteAdminService.obtenerPedidosPorEstado());
        model.addAttribute("productosBajoInventario", reporteAdminService.obtenerProductosBajoInventario());
        model.addAttribute("pedidosRecientes", reporteAdminService.obtenerPedidosRecientes());
        model.addAttribute("umbralInventario", ReporteAdminService.UMBRAL_INVENTARIO_BAJO);

        return "admin-reportes";
    }
}
