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

// @Controller indica que esta clase maneja peticiones HTTP y devuelve vistas (páginas HTML)
@Controller
// @RequestMapping define el prefijo de todas las rutas de este controlador
@RequestMapping("/registro")
public class RegistroController {

    private final ClienteService clienteService;

    public RegistroController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    // @GetMapping maneja peticiones GET — muestra el formulario vacío
    @GetMapping
    public String mostrarFormulario(Model model) {
        // Model lleva datos a la vista Thymeleaf
        // Enviamos un DTO vacío para que Thymeleaf lo enlace con el formulario
        model.addAttribute("registroDTO", new RegistroClienteDTO());
        return "registro"; // busca templates/registro.html
    }

    // @PostMapping maneja peticiones POST — procesa el formulario enviado
    @PostMapping
    public String procesarRegistro(
            // @Valid activa las validaciones del DTO (@NotBlank, @Size, etc.)
            // @ModelAttribute enlaza los campos del formulario con el DTO
            @Valid @ModelAttribute("registroDTO") RegistroClienteDTO dto,
            // BindingResult contiene los errores de validación (si los hay)
            BindingResult resultado,
            Model model) {

        // Si hay errores de validación, volvemos al formulario con los mensajes
        if (resultado.hasErrors()) {
            return "registro";
        }

        try {
            clienteService.registrar(dto);
            // Registro exitoso: redirigimos al login con mensaje de éxito
            return "redirect:/login?registroExitoso";
        } catch (IllegalArgumentException e) {
            // Error de negocio (documento duplicado, contraseñas no coinciden)
            model.addAttribute("error", e.getMessage());
            return "registro";
        }
    }
}
