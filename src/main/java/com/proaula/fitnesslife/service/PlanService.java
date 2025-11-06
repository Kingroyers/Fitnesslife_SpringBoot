package com.proaula.fitnesslife.service;

import com.proaula.fitnesslife.model.Plan;
import com.proaula.fitnesslife.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanService {

    private static final Logger logger = LoggerFactory.getLogger(PlanService.class);
    private final PlanRepository planRepository;

    /**
     * Obtiene todos los planes disponibles ordenados por precio
     */
    public List<Plan> getAllPlans() {
        logger.info("Obteniendo todos los planes disponibles");
        List<Plan> plans = planRepository.findAllByOrderByPriceAsc();
        logger.info("Se encontraron {} planes", plans.size());
        return plans;
    }

    /**
     * Obtiene un plan por su ID
     */
    public Plan getPlanById(String id) {
        logger.info("Buscando plan con ID: {}", id);
        return planRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Plan no encontrado con ID: {}", id);
                    return new RuntimeException("Plan no encontrado con ID: " + id);
                });
    }
}
