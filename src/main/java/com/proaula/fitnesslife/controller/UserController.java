package com.proaula.fitnesslife.controller;

import com.google.zxing.WriterException;
import com.proaula.fitnesslife.service.QrCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.proaula.fitnesslife.model.User;
import com.proaula.fitnesslife.repository.UserRepository;

import java.io.IOException;

@Controller
public class UserController {
    
    private final UserRepository userRepo;
    private final PasswordEncoder encoder;
    
    @Autowired
    private QrCodeService qrCodeService;

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
        
        User savedUser = userRepo.save(user);
        
        try {
            qrCodeService.generateAndSaveQRCode(savedUser);
        } catch (WriterException | IOException e) {
            System.err.println("Error al generar QR code para el usuario: " + e.getMessage());
        }

        return "redirect:/login?registered";
    }
}