package com.huevosdks.controller;

import com.huevosdks.dto.RegistroClienteDTO;
import com.huevosdks.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/registro")
public class RegistroController {

    private final ClienteService clienteService;

    public RegistroController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @ModelAttribute("registroDTO")
    public RegistroClienteDTO registroDTO() {
        return new RegistroClienteDTO();
    }

    @GetMapping({"", "/"})
    public String mostrarFormulario() {
        return "registro";
    }

    @PostMapping({"", "/"})
    public String procesarRegistro(
            @Valid @ModelAttribute("registroDTO") RegistroClienteDTO dto,
            BindingResult resultado,
            Model model
    ) {
        if (resultado.hasErrors()) {
            return "registro";
        }

        try {
            clienteService.registrar(dto);
            return "redirect:/login?registroExitoso=true";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "registro";
        } catch (Exception e) {
            model.addAttribute("error", "No fue posible registrar el cliente. Intenta nuevamente.");
            return "registro";
        }
    }
}
