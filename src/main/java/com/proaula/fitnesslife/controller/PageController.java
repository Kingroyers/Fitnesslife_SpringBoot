package com.proaula.fitnesslife.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.proaula.fitnesslife.repository.UserRepository;

@Controller
public class PageController {

    

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/home")
    public String home(Model model, Principal principal) {
        
        if (principal != null) {
            String email = principal.getName(); // Aquí tienes el correo del usuario autenticado

            userRepository.findByEmail(email).ifPresentOrElse(user -> {
                model.addAttribute("name", user.getName());
                model.addAttribute("lastname", user.getLastname());
                model.addAttribute("identificacion", user.getIdentificacion());
                model.addAttribute("plan", user.getPlan());
            }, () -> {
                model.addAttribute("name", "Usuario no encontrado");
            });

        } else {
            model.addAttribute("username", "Invitado");
        }

        return "client/home";
    }
    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {

        if (principal != null) {
            String email = principal.getName(); // Aquí tienes el correo del usuario autenticado

            userRepository.findByEmail(email).ifPresentOrElse(user -> {
                model.addAttribute("name", user.getName());
                model.addAttribute("lastname", user.getLastname());
                model.addAttribute("identificacion", user.getIdentificacion());
                model.addAttribute("plan", user.getPlan());
            }, () -> {
                model.addAttribute("name", "Usuario no encontrado");
            });

        } else {
            model.addAttribute("username", "Invitado");
        }

        return "admin/dashboard";
    }

    @GetMapping("/payment")
    public String payment() {
        return "client/payment";
    }

    @GetMapping("/plan")
    public String plan() {
        return "client/plan";
    }

    @GetMapping("/qr-code")
    public String qrCode() {
        return "client/qr-code";
    }

    @GetMapping("/user-profile")
    public String userProfile() {
        return "client/user-profile";
    }

}
