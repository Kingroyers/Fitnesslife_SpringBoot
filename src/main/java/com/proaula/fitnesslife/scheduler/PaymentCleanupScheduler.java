package com.proaula.fitnesslife.scheduler;

import com.proaula.fitnesslife.model.Payment;
import com.proaula.fitnesslife.model.User;
import com.proaula.fitnesslife.repository.PaymentRepository;
import com.proaula.fitnesslife.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentCleanupScheduler {

    private final PaymentRepository paymentRepository;
    private final UserService userService;

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void cleanupExpiredPlans() {
        log.info("Iniciando limpieza de planes expirados");
        
        try {
            LocalDateTime now = LocalDateTime.now();
            
            List<Payment> expiredPayments = paymentRepository.findExpiredPayments(now);
            
            log.info("Se encontraron {} planes expirados", expiredPayments.size());
            
            for (Payment payment : expiredPayments) {
                try {
                    User user = payment.getUser();
                    
                    if (user.getPlan() != null && 
                        user.getPlan().equals(payment.getPlan().getPlanName())) {
                        
                        log.info("Removiendo plan expirado '{}' del usuario: {}", 
                            payment.getPlan().getPlanName(), 
                            user.getEmail());
                        
                        user.setPlan(null);
                        userService.save(user);
                        
                        payment.setStatus("EXPIRED");
                        paymentRepository.save(payment);
                        
                        log.info("Plan removido exitosamente para usuario: {}", user.getEmail());
                    }
                    
                } catch (Exception e) {
                    log.error("Error procesando pago expirado ID: {}", payment.getId(), e);
                }
            }
            
            log.info("Limpieza de planes expirados completada");
            
        } catch (Exception e) {
            log.error("Error en limpieza de planes expirados", e);
        }
    }
}