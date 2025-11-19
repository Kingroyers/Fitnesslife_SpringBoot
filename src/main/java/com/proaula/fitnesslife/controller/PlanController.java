package com.proaula.fitnesslife.controller;

import com.proaula.fitnesslife.model.Payment;
import com.proaula.fitnesslife.model.Plan;
import com.proaula.fitnesslife.model.User;
import com.proaula.fitnesslife.service.PaymentService;
import com.proaula.fitnesslife.service.PlanService;
import com.proaula.fitnesslife.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/plan")
@RequiredArgsConstructor
public class PlanController {

    private static final Logger logger = LoggerFactory.getLogger(PlanController.class);
    private final PlanService planService;
    private final PaymentService paymentService;
    private final UserService userService;

    @GetMapping
    public String showPlans(
            Model model,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(value = "success", required = false) String success) {

        try {
            logger.info("Cargando página de planes para usuario: {}", userDetails.getUsername());

            User currentUser = userService.getUserOrThrow(userDetails.getUsername());

            Optional<Payment> activePaymentOpt = paymentService.getActivePayment(currentUser.getId());

            if (activePaymentOpt.isPresent()) {
                Payment activePayment = activePaymentOpt.get();

                logger.info("Usuario {} tiene plan activo: {}",
                        currentUser.getEmail(),
                        activePayment.getPlan().getPlanName());

                model.addAttribute("hasActivePlan", true);
                model.addAttribute("activePayment", activePayment);
                model.addAttribute("activePlan", activePayment.getPlan());
                model.addAttribute("validFrom", activePayment.getValidFrom());
                model.addAttribute("validUntil", activePayment.getValidUntil());

                if ("true".equals(success)) {
                    model.addAttribute("showSuccessAlert", true);
                }

            } else {
                logger.info("Usuario {} no tiene plan activo, mostrando planes disponibles",
                        currentUser.getEmail());

                List<Plan> availablePlans = planService.getAllPlans();

                model.addAttribute("hasActivePlan", false);
                model.addAttribute("availablePlans", availablePlans);

                if (currentUser.getPlan() != null && !currentUser.getPlan().isEmpty()) {
                    model.addAttribute("planExpired", true);
                }
            }

            logger.info("Página de planes cargada exitosamente");
            return "client/plan";

        } catch (Exception e) {
            logger.error("Error al cargar planes: {}", e.getMessage(), e);
            model.addAttribute("error", "Error al cargar los planes disponibles");
            return "client/plan";
        }
    }
}