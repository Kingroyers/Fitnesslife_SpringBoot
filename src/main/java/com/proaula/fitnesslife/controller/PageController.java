package com.proaula.fitnesslife.controller;

import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import com.proaula.fitnesslife.service.UserService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PageController {

    private final UserService userService;

    private static final String VIEW_INDEX = "index";
    private static final String VIEW_LOGIN = "auth/login";
    private static final String VIEW_HOME = "client/home";
    private static final String VIEW_DASHBOARD = "admin/dashboard";
    private static final String VIEW_QR_CODE = "client/qr-code";
    private static final String VIEW_USER_PROFILE = "client/user-profile";

    /**
     * Este método se ejecuta ANTES de cada request en este controlador
     * Carga automáticamente el usuario autenticado en el modelo
     */
    @ModelAttribute
    public void addUserToModel(Model model, Principal principal) {
        if (principal != null) {
            String email = principal.getName();
            userService.findByEmail(email).ifPresentOrElse(
                user -> model.addAttribute("currentUser", user),
                () -> model.addAttribute("error", "Usuario no encontrado")
            );
        }
    }

    @GetMapping("/")
    public String index() {
        return VIEW_INDEX;
    }

    @GetMapping("/login")
    public String login() {
        return VIEW_LOGIN;
    }

    @GetMapping("/home")
    public String home() {
        return VIEW_HOME;
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return VIEW_DASHBOARD;
    }

    @GetMapping("/qr-code")
    public String qrCode() {
        return VIEW_QR_CODE;
    }

    @GetMapping("/user-profile")
    public String userProfile() {
        return VIEW_USER_PROFILE;
    }
}