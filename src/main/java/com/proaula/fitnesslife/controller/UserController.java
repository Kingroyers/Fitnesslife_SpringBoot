package com.proaula.fitnesslife.controller;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.proaula.fitnesslife.model.User;
import com.proaula.fitnesslife.repository.UserRepository;

public class UserController {
    
    private final UserRepository userRepo;
    private final PasswordEncoder encoder;

    public UserController(UserRepository userRepo, PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.encoder = encoder;
    }

     @PostMapping("/register")
    public String register(@ModelAttribute User user, Model model) {

        if (userRepo.findByEmail(user.getEmail()).isPresent()) {
            model.addAttribute("error", "El usuario ya existe");
            return "register";
        }
        user.setPassword(encoder.encode(user.getPassword()));
        user.setRol("USER");
        userRepo.save(user);

        return "redirect:/login?registered";
    }

}
