package com.proaula.fitnesslife.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.proaula.fitnesslife.model.FunctionalTraining;
import com.proaula.fitnesslife.model.User;
import com.proaula.fitnesslife.repository.UserRepository;
import com.proaula.fitnesslife.service.FunctionalTrainingService;
import com.proaula.fitnesslife.service.UserService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PageController {

    private final UserService userService;

    @Autowired
    private UserRepository userRepository;

    private final FunctionalTrainingService service;

    private static final String VIEW_INDEX = "index";
    private static final String VIEW_LOGIN = "auth/login";
    private static final String VIEW_HOME = "client/home";
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
                    () -> model.addAttribute("error", "Usuario no encontrado"));
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
    public String home(Model model, Principal principal) {

        String email = principal.getName();
        User user = userRepository.findByEmail(email).orElse(null);

        if (user != null) {
            model.addAttribute("identification", user.getIdentification());
            List<FunctionalTraining> misClases = service.getTrainingsByUser(email);
            model.addAttribute("confirmedClasses", misClases);
        } else {
            model.addAttribute("identification", null);
        }
        model.addAttribute("trainings", service.getAllTrainings());

        return VIEW_HOME;
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