package com.proaula.fitnesslife.controller;

import com.proaula.fitnesslife.model.Payment;
import com.proaula.fitnesslife.service.PaymentService;
import com.proaula.fitnesslife.service.UserService;
import com.proaula.fitnesslife.service.EpaycoValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final EpaycoValidationService epaycoValidationService;
    private final UserService userService;

    @PostMapping("/payment/confirmation")
    public ResponseEntity<String> paymentConfirmation(@RequestParam Map<String, String> params) {
        log.info("Recibiendo confirmación de pago desde ePayco");
        log.debug("Parámetros recibidos: {}", params);

        try {
            Payment payment = paymentService.processPaymentConfirmation(params);
            log.info("Pago procesado exitosamente: {}", payment.getId());
            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            log.error("Error procesando confirmación de pago", e);
            return ResponseEntity.internalServerError().body("ERROR: " + e.getMessage());
        }
    }

    @GetMapping("/payment/response")
    public String paymentResponse(@RequestParam("ref_payco") String refPayco, Model model) {
        log.info("Usuario redirigido después del pago. ref_payco: {}", refPayco);

        try {
            Map<String, Object> epaycoData = epaycoValidationService.getTransactionData(refPayco);
            
            if (epaycoData == null || !epaycoData.containsKey("success") || 
                !(Boolean) epaycoData.get("success")) {
                log.error("No se pudo obtener información de la transacción desde ePayco");
                model.addAttribute("error", "No se pudo verificar el estado de la transacción");
                return "payment/response";
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) epaycoData.get("data");
            
            String transactionRef = convertToString(data.get("x_ref_payco"));
            String transactionId = convertToString(data.get("x_transaction_id"));
            String transactionDate = convertToString(data.get("x_transaction_date"));
            String transactionStatus = convertToString(data.get("x_response"));
            String statusReason = convertToString(data.get("x_response_reason_text"));
            String bankName = convertToString(data.get("x_bank_name"));
            String amount = convertToString(data.get("x_amount"));
            String currency = convertToString(data.get("x_currency_code"));
            String externalInvoice = convertToString(data.get("x_id_invoice"));
            
            Payment payment = paymentService.getPaymentByExternalInvoice(externalInvoice)
                    .orElse(null);
            
            String planName = "Plan no disponible";
            if (payment != null && payment.getPlan() != null) {
                planName = payment.getPlan().getPlanName();
            }

            model.addAttribute("transactionRef", transactionRef);
            model.addAttribute("transactionId", transactionId);
            model.addAttribute("transactionDate", transactionDate);
            model.addAttribute("transactionStatus", transactionStatus);
            model.addAttribute("statusReason", statusReason);
            model.addAttribute("bankName", bankName);
            model.addAttribute("amount", amount);
            model.addAttribute("currency", currency);
            model.addAttribute("planName", planName);

            log.info("Datos de transacción cargados exitosamente para ref_payco: {}", refPayco);
            
            return "client/response";
            
        } catch (Exception e) {
            log.error("Error mostrando respuesta de pago", e);
            model.addAttribute("error", "Error al procesar la respuesta del pago: " + e.getMessage());
            return "client/response";
        }
    }

    @PostMapping("/api/payment/create")
    @ResponseBody
    public ResponseEntity<Payment> createPayment(@RequestBody Map<String, String> request) {
        log.info("Creando pago pendiente");

        try {
            String userId = request.get("userId");
            String planId = request.get("planId");
            String externalInvoice = request.get("externalInvoice");

            Payment payment = paymentService.createPendingPayment(userId, planId, externalInvoice);
            return ResponseEntity.ok(payment);
        } catch (Exception e) {
            log.error("Error creando pago pendiente", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/payment-history")
    public String paymentHistory(Model model, 
                                @AuthenticationPrincipal org.springframework.security.core.userdetails.User authUser) {
        log.info("Consultando historial de pagos para: {}", authUser.getUsername());

        try {
            com.proaula.fitnesslife.model.User user = userService.getUserOrThrow(authUser.getUsername());
            
            var payments = paymentService.getUserPayments(user.getId());
            
            model.addAttribute("payments", payments);
            
            log.info("Se encontraron {} pagos para el usuario", payments.size());
            
        } catch (Exception e) {
            log.error("Error consultando historial de pagos", e);
            model.addAttribute("payments", java.util.Collections.emptyList());
        }

        return "client/payment-history";
    }
    
    private String convertToString(Object value) {
        if (value == null) {
            return null;
        }
        return value.toString();
    }
}