package com.huevosdks.controller;

import com.huevosdks.entity.Producto;
import com.huevosdks.service.ProductoService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CatalogoController {

    private final ProductoService productoService;

    public CatalogoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping("/catalogo")
    public String verCatalogo(
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String buscar,
            Authentication authentication,
            Model model
    ) {
        model.addAttribute("titulo", "Catálogo de productos");
        model.addAttribute("usuario", authentication.getName());
        model.addAttribute("productos", productoService.listarCatalogo(tipo, buscar));
        model.addAttribute("tipos", Producto.TipoProducto.values());
        model.addAttribute("tipoSeleccionado", tipo);
        model.addAttribute("busqueda", buscar);

        return "catalogo";
    }
}
