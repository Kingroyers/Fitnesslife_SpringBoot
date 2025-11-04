package com.proaula.fitnesslife.controller;

import java.security.Principal;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.proaula.fitnesslife.model.User;
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
                model.addAttribute("identificacion", user.getIdentification());

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
                model.addAttribute("identificacion", user.getIdentification());
                model.addAttribute("phone", user.getPhone());
                model.addAttribute("email", user.getEmail());
                model.addAttribute("bloodType", user.getBloodType());
                model.addAttribute("lastlogin", user.getLastlogin());
                model.addAttribute("photoProfile", user.getPhotoProfile());
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
    public String plan(Model model, Principal principal) {

        return "client/plan";
    }

    @GetMapping("/qr-code")
    public String qrCode() {
        return "client/qr-code";
    }

    @GetMapping("/user-profile")
    public String userProfile(Model model, Principal principal) {

        if (principal != null) {
            String email = principal.getName();

            userRepository.findByEmail(email).ifPresentOrElse(user -> {
                // 🕓 Formateamos la fecha aquí
                 Date dateCreated = Date.from(user.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant());

                model.addAttribute("user", user);
                model.addAttribute("dateCreated", dateCreated); // ✅ agregamos la fecha formateada
            }, () -> {
                model.addAttribute("error", "Usuario no encontrado");
            });

        } else {
            model.addAttribute("username", "Invitado");
        }

        return "client/user-profile";
    }

}
