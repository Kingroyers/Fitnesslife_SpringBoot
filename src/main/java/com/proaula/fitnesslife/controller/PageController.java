package com.proaula.fitnesslife.controller;

import java.security.Principal;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.proaula.fitnesslife.model.FunctionalTraining;
import com.proaula.fitnesslife.repository.UserRepository;
import com.proaula.fitnesslife.service.FunctionalTrainingService;
import com.proaula.fitnesslife.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PageController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final FunctionalTrainingService service;

    private static final String VIEW_INDEX = "index";
    private static final String VIEW_LOGIN = "auth/login";
    private static final String VIEW_HOME = "client/home";
    private static final String VIEW_PAYMENT = "client/payment";
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
    public String home(Model model, Principal principal) {
        List<FunctionalTraining> trainings = service.getAllTrainings();
        model.addAttribute("trainings", trainings);

        if (principal != null) {
            String email = principal.getName();

            userRepository.findByEmail(email).ifPresentOrElse(user -> {
                model.addAttribute("name", user.getName());
                model.addAttribute("lastname", user.getLastname());
                model.addAttribute("identificacion", user.getIdentification());
                model.addAttribute("plan", user.getPlan());
                model.addAttribute("role", user.getRole());

                List<FunctionalTraining> confirmedClasses = trainings.stream()
                        .filter(t -> t.getUserIds() != null && t.getUserIds().contains(user.getIdentification()))
                        .toList();

                model.addAttribute("confirmedClasses", confirmedClasses);
            }, () -> {
                model.addAttribute("name", "Usuario no encontrado");
            });

        } else {
            model.addAttribute("username", "Invitado");
        }

        return VIEW_HOME;
    }

    @GetMapping("/payment")
    public String payment() {
        return VIEW_PAYMENT;
    }

    @GetMapping("/qr-code")
    public String qrCode() {
        return VIEW_QR_CODE;
    }

    @GetMapping("/user-profile")
    public String userProfile(Model model, Principal principal) {
        if (principal != null) {
            String email = principal.getName();

            userRepository.findByEmail(email).ifPresentOrElse(user -> {
                Date dateCreated = Date.from(user.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant());
                model.addAttribute("user", user);
                model.addAttribute("dateCreated", dateCreated);
                model.addAttribute("isActive", user.isActive());
            }, () -> {
                model.addAttribute("error", "Usuario no encontrado");
            });

        } else {
            model.addAttribute("username", "Invitado");
        }

        return VIEW_USER_PROFILE;
    }
}
