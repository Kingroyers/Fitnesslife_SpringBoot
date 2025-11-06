package com.proaula.fitnesslife.config;

import com.proaula.fitnesslife.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalUserAdvice {

    private final UserService userService;

    /**
     * Agrega el usuario autenticado al modelo para todas las vistas Thymeleaf.
     */
    @ModelAttribute
    public void addCurrentUser(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {

            // Manejar Optional<User> de forma segura
            userService.findByEmail(authentication.getName())
                       .ifPresent(user -> model.addAttribute("currentUser", user));
        }
    }
}


