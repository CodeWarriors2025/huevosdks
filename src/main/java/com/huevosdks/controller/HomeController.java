package com.huevosdks.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
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
    public String admin() {
        return "redirect:/admin/pedidos";
    }

    @GetMapping("/operador")
    public String operador() {
        return "redirect:/operador/pedidos";
    }
}