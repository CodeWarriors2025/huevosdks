package com.huevosdks.controller;

import com.huevosdks.dto.OperadorAdminFormDTO;
import com.huevosdks.entity.Usuario;
import com.huevosdks.service.UsuarioAdminService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminUsuarioController {

    private final UsuarioAdminService usuarioAdminService;

    public AdminUsuarioController(UsuarioAdminService usuarioAdminService) {
        this.usuarioAdminService = usuarioAdminService;
    }

    @GetMapping("/admin/usuarios")
    public String listarUsuariosYClientes(
            @RequestParam(required = false) String rol,
            Authentication authentication,
            Model model
    ) {
        model.addAttribute("titulo", "Gestión de usuarios y clientes");
        model.addAttribute("usuario", authentication.getName());
        model.addAttribute("usuarios", usuarioAdminService.listarUsuarios(rol));
        model.addAttribute("clientes", usuarioAdminService.listarClientes());
        model.addAttribute("roles", Usuario.Rol.values());
        model.addAttribute("rolSeleccionado", rol);

        return "admin-usuarios";
    }

    @GetMapping("/admin/usuarios/nuevo-operador")
    public String mostrarFormularioNuevoOperador(Model model) {
        OperadorAdminFormDTO operadorForm = new OperadorAdminFormDTO();
        operadorForm.setActivo(true);

        model.addAttribute("titulo", "Nuevo operador");
        model.addAttribute("operadorForm", operadorForm);

        return "admin-operador-form";
    }

    @PostMapping("/admin/usuarios/nuevo-operador")
    public String crearOperador(
            @Valid OperadorAdminFormDTO operadorForm,
            BindingResult resultado,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (resultado.hasErrors()) {
            model.addAttribute("titulo", "Nuevo operador");
            return "admin-operador-form";
        }

        try {
            usuarioAdminService.crearOperador(operadorForm);
            redirectAttributes.addFlashAttribute("mensaje", "Operador creado correctamente.");
            return "redirect:/admin/usuarios?rol=OPERADOR";
        } catch (IllegalArgumentException e) {
            model.addAttribute("titulo", "Nuevo operador");
            model.addAttribute("error", e.getMessage());
            return "admin-operador-form";
        }
    }

    @PostMapping("/admin/usuarios/{usuarioId}/cambiar-estado")
    public String cambiarEstadoUsuario(
            @PathVariable Long usuarioId,
            @RequestParam boolean activo,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        try {
            usuarioAdminService.cambiarEstadoUsuario(usuarioId, activo, authentication.getName());
            redirectAttributes.addFlashAttribute("mensaje", "Estado del usuario actualizado correctamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/usuarios";
    }
}
