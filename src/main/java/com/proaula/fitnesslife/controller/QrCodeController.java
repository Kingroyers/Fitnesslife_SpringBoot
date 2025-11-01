package com.proaula.fitnesslife.controller;

import com.google.zxing.WriterException;
import com.proaula.fitnesslife.model.User;
import com.proaula.fitnesslife.repository.UserRepository;
import com.proaula.fitnesslife.service.QrCodeService;
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
    
    @Autowired
    private QrCodeService qrCodeService;
    
    @Autowired
    private UserRepository userRepository;

    /**
     * Muestra la página del QR code
     */
    @GetMapping("/qrcode")
    public String qrCodePage(Model model) {
        try {
            // Obtener el usuario autenticado
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            System.out.println("Email autenticado: " + email); // Debug
            
            Optional<User> userOpt = userRepository.findByEmail(email);
            
            if (userOpt.isEmpty()) {
                System.out.println("Usuario no encontrado con email: " + email); // Debug
                model.addAttribute("error", "Usuario no encontrado");
                return "error";
            }
            
            User user = userOpt.get();
            System.out.println("Usuario encontrado: " + user.getName() + ", ID: " + user.getIdentificacion()); // Debug
            
            // Verificar si el usuario ya tiene un QR generado
            if (!qrCodeService.qrCodeExists(user)) {
                System.out.println("Generando QR para usuario: " + user.getIdentificacion()); // Debug
                try {
                    // Generar el QR si no existe
                    qrCodeService.generateAndSaveQRCode(user);
                    System.out.println("QR generado exitosamente"); // Debug
                } catch (WriterException | IOException e) {
                    System.err.println("Error al generar QR: " + e.getMessage()); // Debug
                    e.printStackTrace();
                    model.addAttribute("error", "Error al generar el código QR: " + e.getMessage());
                    return "error";
                }
            } else {
                System.out.println("QR ya existe para usuario: " + user.getIdentificacion()); // Debug
            }
            
            model.addAttribute("user", user);
            System.out.println("Usuario agregado al modelo correctamente"); // Debug
            return "client/qr-code";
            
        } catch (Exception e) {
            System.err.println("Error general en qrCodePage: " + e.getMessage()); // Debug
            e.printStackTrace();
            model.addAttribute("error", "Error inesperado: " + e.getMessage());
            return "error";
        }
    }

    /**
     * Endpoint para obtener la imagen del QR code del usuario autenticado
     */
    @GetMapping(value = "/api/qr/image", produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public ResponseEntity<byte[]> getQRCodeImage() {
        try {
            // Obtener el usuario autenticado
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            Optional<User> userOpt = userRepository.findByEmail(email);
            
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            
            User user = userOpt.get();
            
            // Obtener la imagen del QR
            BufferedImage qrImage = qrCodeService.getQRCodeImage(user);
            
            if (qrImage == null) {
                // Si no existe, generarlo
                qrCodeService.generateAndSaveQRCode(user);
                qrImage = qrCodeService.getQRCodeImage(user);
            }
            
            // Convertir BufferedImage a byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(qrImage, "png", baos);
            byte[] imageBytes = baos.toByteArray();
            
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(imageBytes);
                    
        } catch (IOException | WriterException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint para regenerar el QR code del usuario
     */
    @PostMapping("/api/qr/refresh")
    @ResponseBody
    public ResponseEntity<?> refreshQRCode() {
        try {
            // Obtener el usuario autenticado
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            Optional<User> userOpt = userRepository.findByEmail(email);
            
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("{\"error\": \"Usuario no encontrado\"}");
            }
            
            User user = userOpt.get();
            
            // Regenerar el QR code
            qrCodeService.regenerateQRCode(user);
            
            return ResponseEntity.ok()
                    .body("{\"success\": true, \"message\": \"Código QR actualizado correctamente\"}");
                    
        } catch (WriterException | IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Error al regenerar el código QR\"}");
        }
    }
}