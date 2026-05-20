package com.huevosdks.controller;

import com.huevosdks.dto.PerfilUsuarioDTO;
import com.huevosdks.service.PerfilUsuarioService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PerfilUsuarioController {

    private final PerfilUsuarioService perfilUsuarioService;

    public PerfilUsuarioController(PerfilUsuarioService perfilUsuarioService) {
        this.perfilUsuarioService = perfilUsuarioService;
    }

    @GetMapping("/perfil")
    public String verPerfil(
            Authentication authentication,
            Model model
    ) {
        PerfilUsuarioDTO perfil = perfilUsuarioService.obtenerPerfil(authentication.getName());

        model.addAttribute("titulo", "Mi perfil");
        model.addAttribute("perfil", perfil);

        return "perfil";
    }

    @PostMapping("/perfil")
    public String actualizarPerfil(
            @Valid PerfilUsuarioDTO perfil,
            BindingResult resultado,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (resultado.hasErrors()) {
            model.addAttribute("titulo", "Mi perfil");
            return "perfil";
        }

        try {
            perfilUsuarioService.actualizarPerfil(authentication.getName(), perfil);
            redirectAttributes.addFlashAttribute("mensaje", "Perfil actualizado correctamente.");
            return "redirect:/perfil";
        } catch (IllegalArgumentException e) {
            model.addAttribute("titulo", "Mi perfil");
            model.addAttribute("error", e.getMessage());
            return "perfil";
        }
    }
}