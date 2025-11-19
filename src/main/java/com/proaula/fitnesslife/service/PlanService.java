package com.proaula.fitnesslife.service;

import com.proaula.fitnesslife.model.Plan;
import com.proaula.fitnesslife.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanService {

    private static final Logger logger = LoggerFactory.getLogger(PlanService.class);
    private final PlanRepository planRepository;

    public List<Plan> getAllPlans() {
        logger.info("Obteniendo todos los planes disponibles");
        List<Plan> plans = planRepository.findAllByOrderByPriceAsc();
        logger.info("Se encontraron {} planes", plans.size());
        return plans;
    }

    public Plan getPlanById(String id) {
        logger.info("Buscando plan con ID: {}", id);
        return planRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Plan no encontrado con ID: {}", id);
                    return new RuntimeException("Plan no encontrado con ID: " + id);
                });
    }

    public Plan createPlan(Plan plan) {
        logger.info("Creando nuevo plan: {}", plan.getPlanName());

        if (plan.getCreatedAt() == null) {
            plan.setCreatedAt(LocalDateTime.now());
        }
        plan.setUpdatedAt(LocalDateTime.now());

        Plan savedPlan = planRepository.save(plan);
        logger.info("Plan creado exitosamente con ID: {}", savedPlan.getId());
        return savedPlan;
    }

    public Plan updatePlan(Plan plan) {
        logger.info("Actualizando plan con ID: {}", plan.getId());

        Plan existingPlan = getPlanById(plan.getId());

        existingPlan.setPlanName(plan.getPlanName());
        existingPlan.setPrice(plan.getPrice());
        existingPlan.setCurrency(plan.getCurrency());
        existingPlan.setDurationDays(plan.getDurationDays());
        existingPlan.setBadge(plan.getBadge());
        existingPlan.setBenefits(plan.getBenefits());
        existingPlan.setUpdatedAt(LocalDateTime.now());

        Plan updatedPlan = planRepository.save(existingPlan);
        logger.info("Plan actualizado exitosamente: {}", updatedPlan.getPlanName());
        return updatedPlan;
    }

    public void deletePlan(String id) {
        logger.info("Eliminando plan con ID: {}", id);

        Plan plan = getPlanById(id);

        planRepository.deleteById(id);
        logger.info("Plan eliminado exitosamente: {}", plan.getPlanName());
    }
}