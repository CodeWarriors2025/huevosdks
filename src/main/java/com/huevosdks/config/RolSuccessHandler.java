package com.huevosdks.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

// Este handler decide a dónde redirigir al usuario según su rol después del login exitoso
@Component
public class RolSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        String rol = authentication.getAuthorities().iterator().next().getAuthority();

        switch (rol) {
            case "ROLE_CLIENTE"  -> response.sendRedirect("/catalogo");
            case "ROLE_OPERADOR" -> response.sendRedirect("/operador");
            case "ROLE_ADMIN"    -> response.sendRedirect("/admin");
            default              -> response.sendRedirect("/login?error=true");
        }
    }
}
