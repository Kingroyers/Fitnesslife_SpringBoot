package com.proaula.fitnesslife.service;

import com.proaula.fitnesslife.model.Payment;
import com.proaula.fitnesslife.model.Plan;
import com.proaula.fitnesslife.model.User;
import com.proaula.fitnesslife.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserService userService;
    private final PlanService planService;

    @Value("${epayco.public-key:}")
    private String epaycoPublicKey;

    @Value("${epayco.private-key:}")
    private String epaycoPrivateKey;

    @Value("${epayco.customer-id:}")
    private String epaycoCustomerId;

    @Transactional
    public Payment createPendingPayment(String userId, String planId, String externalInvoice) {
        log.info("Creando pago pendiente para usuario: {} y plan: {}", userId, planId);

        User user = userService.getUserByIdOrThrow(userId);
        Plan plan = planService.getPlanById(planId);

        Payment payment = Payment.builder()
                .user(user)
                .plan(plan)
                .externalInvoice(externalInvoice)
                .amount(plan.getPrice())
                .currency(plan.getCurrency())
                .status("PENDING")
                .build();

        Payment saved = paymentRepository.save(payment);
        log.info("Pago pendiente creado con ID: {}", saved.getId());
        return saved;
    }

    @Transactional
    public Payment processPaymentConfirmation(Map<String, String> params) {
        log.info("Procesando confirmación de pago para ref_payco: {}", params.get("x_ref_payco"));

        String refPayco = params.get("x_ref_payco");
        String externalInvoiceId = params.get("x_id_invoice");
        String transactionId = params.get("x_transaction_id");
        String amount = params.get("x_amount");
        String currencyCode = params.get("x_currency_code");
        String signature = params.get("x_signature");
        String responseCode = params.get("x_cod_response");
        String responseText = params.get("x_response");
        String responseReason = params.get("x_response_reason_text");
        String approvalCode = params.get("x_approval_code");
        String bankName = params.get("x_bank_name");
        String franchise = params.get("x_franchise");
        String transactionDate = params.get("x_transaction_date");

        String expectedSignature = generateSignature(refPayco, transactionId, amount, currencyCode);
    
        log.debug("DEBUG HASH: Cadena construida: {}^{}^{}^{}^{}^{} | Hash calculado: {} | Hash Recibido: {}", 
                  epaycoCustomerId, epaycoPrivateKey, refPayco, transactionId, amount, currencyCode, 
                  expectedSignature, signature);
        
        if (!expectedSignature.equals(signature)) {
            log.error("Firma inválida. Esperada: {}, Recibida: {}", expectedSignature, signature);
            // si esto falla, el WebHook lanza una excepción y NO actualiza la DB.
            throw new RuntimeException("Firma de pago inválida"); 
        }

        Payment payment = paymentRepository.findByExternalInvoice(externalInvoiceId)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado: " + externalInvoiceId)); 
        

        if (!payment.getAmount().equals(Double.parseDouble(amount))) {
            log.error("Monto no coincide. Esperado: {}, Recibido: {}", payment.getAmount(), amount);
            throw new RuntimeException("El monto del pago no coincide");
        }

        String status = determineStatus(responseCode);
        payment.setStatus(status);
        payment.setTransactionId(transactionId);
        payment.setApprovalCode(approvalCode);
        payment.setBankName(bankName);
        payment.setFranchise(franchise);
        payment.setResponseCode(responseCode);
        payment.setResponseText(responseText);
        payment.setResponseReason(responseReason);
        payment.setSignature(signature);
        payment.setTransactionDate(parseTransactionDate(transactionDate));

        if ("ACCEPTED".equals(status)) {
            LocalDateTime now = LocalDateTime.now();
            payment.setValidFrom(now);
            payment.setValidUntil(now.plusDays(payment.getPlan().getDurationDays()));
            
            User user = payment.getUser();
            user.setPlan(payment.getPlan().getPlanName());
            userService.save(user);
            
            log.info("Pago aceptado. Usuario {} actualizado con plan {}", user.getId(), user.getPlan());
        }

        Payment saved = paymentRepository.save(payment);
        log.info("Pago procesado con estado: {}", saved.getStatus());
        return saved;
    }

    private String generateSignature(String refPayco, String transactionId, String amount, String currency) {
        String data = epaycoCustomerId + "^" + epaycoPrivateKey + "^" + refPayco + "^" + 
                      transactionId + "^" + amount + "^" + currency;
        
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            log.error("Error generando firma SHA256", e);
            throw new RuntimeException("Error generando firma", e);
        }
    }

    private String determineStatus(String responseCode) {
        return switch (responseCode) {
            case "1" -> "ACCEPTED";
            case "2" -> "REJECTED";
            case "3" -> "PENDING";
            case "4" -> "FAILED";
            default -> "UNKNOWN";
        };
    }

    private LocalDateTime parseTransactionDate(String dateStr) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return LocalDateTime.parse(dateStr, formatter);
        } catch (Exception e) {
            log.warn("Error parseando fecha de transacción: {}", dateStr);
            return LocalDateTime.now();
        }
    }

    public List<Payment> getUserPayments(String userId) {
        User user = userService.getUserByIdOrThrow(userId);
        return paymentRepository.findByUser(user);
    }

    public Optional<Payment> getActivePayment(String userId) {
        User user = userService.getUserByIdOrThrow(userId);
        LocalDateTime now = LocalDateTime.now();
        
        List<Payment> activePayments = paymentRepository
                .findByUserAndStatusAndValidFromLessThanEqualAndValidUntilGreaterThanEqual(
                        user, "ACCEPTED", now, now);
        
        return activePayments.isEmpty() ? Optional.empty() : Optional.of(activePayments.get(0));
    }

    public Optional<Payment> getPaymentByExternalInvoice(String externalInvoice) {
        return paymentRepository.findByExternalInvoice(externalInvoice);
    }

    public Optional<Payment> getPaymentByTransactionId(String transactionId) {
        return paymentRepository.findByTransactionId(transactionId);
    }
}