package com.proaula.fitnesslife.config;

import com.proaula.fitnesslife.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

@ControllerAdvice
public class GlobalUserAdvice {

    private final UserService userService;

    // Constructor manual
    public GlobalUserAdvice(UserService userService) {
        this.userService = userService;
    }

    @ModelAttribute
    public void addCurrentUser(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            userService.findByEmail(authentication.getName())
                       .ifPresent(user -> model.addAttribute("currentUser", user));
        }
    }
}
