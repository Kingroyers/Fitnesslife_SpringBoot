package com.proaula.fitnesslife.controller;

import com.google.zxing.WriterException;
import com.proaula.fitnesslife.model.User;
import com.proaula.fitnesslife.repository.UserRepository;
import com.proaula.fitnesslife.service.QrCodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;

@Controller
public class QrCodeController {

    private static final Logger logger = LoggerFactory.getLogger(QrCodeController.class);

    @Autowired
    private QrCodeService qrCodeService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/qrcode")
    public String qrCodePage(Model model) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            logger.debug("Email autenticado: {}", email);

            Optional<User> userOpt = userRepository.findByEmail(email);

            if (userOpt.isEmpty()) {
                logger.warn("Usuario no encontrado con email: {}", email);
                model.addAttribute("error", "Usuario no encontrado");
                return "error";
            }

            User user = userOpt.get();
            logger.info("Usuario encontrado: {} (ID: {})", user.getName(), user.getIdentification());

            if (!qrCodeService.qrCodeExists(user)) {
                logger.info("Generando QR para usuario: {}", user.getIdentification());
                try {
                    qrCodeService.generateAndSaveQRCode(user);
                    logger.info("QR generado exitosamente para usuario: {}", user.getIdentification());
                } catch (WriterException | IOException e) {
                    logger.error("Error al generar QR para usuario {}: {}", user.getIdentification(), e.getMessage(),
                            e);
                    model.addAttribute("error", "Error al generar el código QR: " + e.getMessage());
                    return "error";
                }
            } else {
                logger.debug("QR ya existe para usuario: {}", user.getIdentification());
            }

            model.addAttribute("user", user);
            logger.debug("Usuario agregado al modelo correctamente");
            return "client/qr-code";

        } catch (Exception e) {
            logger.error("Error inesperado en qrCodePage: {}", e.getMessage(), e);
            model.addAttribute("error", "Error inesperado: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping(value = "/api/qr/image", produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public ResponseEntity<byte[]> getQRCodeImage() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            logger.debug("Solicitando imagen QR para: {}", email);

            Optional<User> userOpt = userRepository.findByEmail(email);

            if (userOpt.isEmpty()) {
                logger.warn("Usuario no encontrado para imagen QR: {}", email);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            User user = userOpt.get();
            BufferedImage qrImage = qrCodeService.getQRCodeImage(user);

            if (qrImage == null) {
                logger.info("QR no encontrado, generando nuevo para usuario: {}", user.getIdentification());
                qrCodeService.generateAndSaveQRCode(user);
                qrImage = qrCodeService.getQRCodeImage(user);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(qrImage, "png", baos);
            byte[] imageBytes = baos.toByteArray();

            logger.debug("QR convertido a imagen PNG correctamente");
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(imageBytes);

        } catch (IOException | WriterException e) {
            logger.error("Error al obtener imagen QR: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/api/qr/refresh")
    @ResponseBody
    public ResponseEntity<?> refreshQRCode() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            logger.debug("Solicitud de regeneración de QR para: {}", email);

            Optional<User> userOpt = userRepository.findByEmail(email);

            if (userOpt.isEmpty()) {
                logger.warn("Usuario no encontrado para regenerar QR: {}", email);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("{\"error\": \"Usuario no encontrado\"}");
            }

            User user = userOpt.get();
            qrCodeService.regenerateQRCode(user);
            logger.info("QR regenerado correctamente para usuario: {}", user.getIdentification());

            return ResponseEntity.ok()
                    .body("{\"success\": true, \"message\": \"Código QR actualizado correctamente\"}");

        } catch (WriterException | IOException e) {
            logger.error("Error al regenerar QR: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Error al regenerar el código QR\"}");
        }
    }
}
