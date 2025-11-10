package com.proaula.fitnesslife.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {
    
    private static final String VIEW_DASHBOARD = "admin/dashboard";

    @GetMapping("/dashboard")
    public String dashboard() {
        return VIEW_DASHBOARD;
    }
}
