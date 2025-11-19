package com.proaula.fitnesslife.controller;

import com.proaula.fitnesslife.model.Plan;
import com.proaula.fitnesslife.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/plans")
@RequiredArgsConstructor
public class AdminPlanController {

    private static final Logger logger = LoggerFactory.getLogger(AdminPlanController.class);
    private final PlanService planService;

    @GetMapping
    public String showPlansManagement(Model model) {
        try {
            logger.info("Cargando gestión de planes");
            List<Plan> plans = planService.getAllPlans();
            model.addAttribute("plans", plans);
            model.addAttribute("newPlan", new Plan());
            logger.info("Se cargaron {} planes", plans.size());
            return "admin/plans";
        } catch (Exception e) {
            logger.error("Error al cargar planes: {}", e.getMessage(), e);
            model.addAttribute("error", "Error al cargar los planes");
            return "admin/plans";
        }
    }

    @PostMapping("/create")
    public String createPlan(@ModelAttribute Plan plan, RedirectAttributes redirectAttributes) {
        try {
            logger.info("Creando nuevo plan: {}", plan.getPlanName());
            planService.createPlan(plan);
            redirectAttributes.addFlashAttribute("success", "Plan creado exitosamente");
            logger.info("Plan creado con éxito: {}", plan.getPlanName());
        } catch (Exception e) {
            logger.error("Error al crear plan: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Error al crear el plan: " + e.getMessage());
        }
        return "redirect:/admin/plans";
    }

    @PostMapping("/update/{id}")
    public String updatePlan(
            @PathVariable String id,
            @ModelAttribute Plan plan,
            RedirectAttributes redirectAttributes) {
        try {
            logger.info("Actualizando plan con ID: {}", id);
            plan.setId(id);
            planService.updatePlan(plan);
            redirectAttributes.addFlashAttribute("success", "Plan actualizado exitosamente");
            logger.info("Plan actualizado con éxito: {}", plan.getPlanName());
        } catch (Exception e) {
            logger.error("Error al actualizar plan: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el plan: " + e.getMessage());
        }
        return "redirect:/admin/plans";
    }

    @PostMapping("/delete/{id}")
    public String deletePlan(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            logger.info("Eliminando plan con ID: {}", id);
            planService.deletePlan(id);
            redirectAttributes.addFlashAttribute("success", "Plan eliminado exitosamente");
            logger.info("Plan eliminado con éxito");
        } catch (Exception e) {
            logger.error("Error al eliminar plan: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el plan: " + e.getMessage());
        }
        return "redirect:/admin/plans";
    }

    @GetMapping("/get/{id}")
    @ResponseBody
    public Plan getPlanById(@PathVariable String id) {
        logger.info("Obteniendo plan con ID: {}", id);
        return planService.getPlanById(id);
    }
}