package com.huevosdks.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String inicio(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String rol = authentication.getAuthorities().iterator().next().getAuthority();

        return switch (rol) {
            case "ROLE_ADMIN" -> "redirect:/admin";
            case "ROLE_OPERADOR" -> "redirect:/operador";
            case "ROLE_CLIENTE" -> "redirect:/catalogo";
            default -> "redirect:/login";
        };
    }

    @GetMapping("/admin")
    public String admin(Authentication authentication, Model model) {
        model.addAttribute("titulo", "Panel Administrador");
        model.addAttribute("usuario", authentication.getName());
        model.addAttribute("rol", "ADMIN");
        return "dashboard";
    }

    @GetMapping("/operador")
    public String operador(Authentication authentication, Model model) {
        model.addAttribute("titulo", "Panel Operador");
        model.addAttribute("usuario", authentication.getName());
        model.addAttribute("rol", "OPERADOR");
        return "dashboard";
    }
}