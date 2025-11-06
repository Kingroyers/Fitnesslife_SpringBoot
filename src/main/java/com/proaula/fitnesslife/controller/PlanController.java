package com.proaula.fitnesslife.controller;

import com.proaula.fitnesslife.model.Plan;
import com.proaula.fitnesslife.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/plan")
@RequiredArgsConstructor
public class PlanController {

    private static final Logger logger = LoggerFactory.getLogger(PlanController.class);
    private final PlanService planService;

    /**
     * Muestra la página de planes con todos los planes disponibles
     */
    @GetMapping
    public String showPlans(Model model) {
        try {
            logger.info("Cargando página de planes");
            
            // Obtener todos los planes desde la BD
            List<Plan> availablePlans = planService.getAllPlans();
            
            // Agregar planes al modelo
            model.addAttribute("availablePlans", availablePlans);
            
            logger.info("Página de planes cargada exitosamente con {} planes", availablePlans.size());
            return "client/plan";
            
        } catch (Exception e) {
            logger.error("Error al cargar planes: {}", e.getMessage());
            model.addAttribute("error", "Error al cargar los planes disponibles");
            return "client/plan";
        }
    }
}
