package com.huevosdks.controller;

import com.huevosdks.dto.ProductoAdminFormDTO;
import com.huevosdks.entity.Producto;
import com.huevosdks.service.ProductoAdminService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AdminProductoController {

    private final ProductoAdminService productoAdminService;

    public AdminProductoController(ProductoAdminService productoAdminService) {
        this.productoAdminService = productoAdminService;
    }

    @GetMapping("/admin/productos")
    public String listarProductos(Model model) {
        model.addAttribute("titulo", "Gestión de productos");
        model.addAttribute("productos", productoAdminService.listarProductosAdmin());

        return "admin-productos";
    }

    @GetMapping("/admin/productos/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        ProductoAdminFormDTO form = new ProductoAdminFormDTO();
        form.setTipo("CUBETA");
        form.setUnidadesPorPresentacion(30);
        form.setCantidadDisponible(0);
        form.setActivo(true);

        prepararFormulario(model, form, "CREAR", null);

        return "admin-producto-form";
    }

    @PostMapping("/admin/productos/nuevo")
    public String crearProducto(
            @Valid ProductoAdminFormDTO productoForm,
            BindingResult resultado,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (resultado.hasErrors()) {
            prepararFormulario(model, productoForm, "CREAR", null);
            return "admin-producto-form";
        }

        try {
            productoAdminService.crearProducto(productoForm);
            redirectAttributes.addFlashAttribute("mensaje", "Producto creado correctamente.");
            return "redirect:/admin/productos";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            prepararFormulario(model, productoForm, "CREAR", null);
            return "admin-producto-form";
        }
    }

    @GetMapping("/admin/productos/{productoId}/editar")
    public String mostrarFormularioEditar(
            @PathVariable Long productoId,
            Model model
    ) {
        ProductoAdminFormDTO form = productoAdminService.obtenerFormularioEdicion(productoId);
        prepararFormulario(model, form, "EDITAR", productoId);

        return "admin-producto-form";
    }

    @PostMapping("/admin/productos/{productoId}/editar")
    public String editarProducto(
            @PathVariable Long productoId,
            @Valid ProductoAdminFormDTO productoForm,
            BindingResult resultado,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (resultado.hasErrors()) {
            prepararFormulario(model, productoForm, "EDITAR", productoId);
            return "admin-producto-form";
        }

        try {
            productoAdminService.editarProducto(productoId, productoForm);
            redirectAttributes.addFlashAttribute("mensaje", "Producto actualizado correctamente.");
            return "redirect:/admin/productos";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            prepararFormulario(model, productoForm, "EDITAR", productoId);
            return "admin-producto-form";
        }
    }

    @PostMapping("/admin/productos/{productoId}/cambiar-estado")
    public String cambiarEstado(
            @PathVariable Long productoId,
            @RequestParam boolean activo,
            RedirectAttributes redirectAttributes
    ) {
        try {
            productoAdminService.cambiarEstadoProducto(productoId, activo);
            redirectAttributes.addFlashAttribute("mensaje", "Estado del producto actualizado.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/productos";
    }

    private void prepararFormulario(
            Model model,
            ProductoAdminFormDTO form,
            String modo,
            Long productoId
    ) {
        model.addAttribute("productoForm", form);
        model.addAttribute("modo", modo);
        model.addAttribute("productoId", productoId);
        model.addAttribute("tipos", Producto.TipoProducto.values());
    }
}
