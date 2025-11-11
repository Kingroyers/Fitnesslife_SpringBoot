package com.proaula.fitnesslife.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.proaula.fitnesslife.model.FunctionalTraining;
import com.proaula.fitnesslife.service.FunctionalTrainingService;

@Controller
public class DashboardController {

    private final FunctionalTrainingService service;

    @Autowired
    public DashboardController(FunctionalTrainingService service) {
        this.service = service;
    }

    private static final String VIEW_DASHBOARD = "admin/dashboard";
    private static final String VIEW_FUNCTIONALTRAINING = "admin/functionalTrainingCrud";

    @GetMapping("/dashboard")
    public String dashboard() {
        return VIEW_DASHBOARD;
    }

    // esto funciona para cargar la vista de entrenamientos funcionales
    @GetMapping("/admin/functionalTraining")
    public String functionalTraining(Model model) {
        List<FunctionalTraining> trainings = service.getAllTrainings();
        model.addAttribute("trainings", trainings);
        model.addAttribute("training", new FunctionalTraining());
        
        return VIEW_FUNCTIONALTRAINING;
    }

}
