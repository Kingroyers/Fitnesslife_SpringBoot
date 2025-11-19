package com.proaula.fitnesslife.controller;

import com.proaula.fitnesslife.model.Access;
import com.proaula.fitnesslife.service.AccessService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequestMapping("/admin/accesses")
@RequiredArgsConstructor
public class AdminAccessController {

    private static final Logger logger = LoggerFactory.getLogger(AdminAccessController.class);
    private final AccessService accessService;

    @GetMapping
    public String showAccessesManagement(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String result,
            @RequestParam(required = false) String search,
            Model model) {
        try {
            logger.info("Cargando gestión de accesos - Página: {}, Tamaño: {}, Resultado: {}, Búsqueda: {}", 
                       page, size, result, search);
            
            Page<Access> accessesPage = accessService.getAccessesPaginated(page, size, result, search);
            
            Map<String, Object> statistics = accessService.getAccessStatistics();
            
            model.addAttribute("accesses", accessesPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", accessesPage.getTotalPages());
            model.addAttribute("totalItems", accessesPage.getTotalElements());
            model.addAttribute("pageSize", size);
            model.addAttribute("resultFilter", result != null ? result : "");
            model.addAttribute("searchTerm", search != null ? search : "");
            
            model.addAttribute("totalAccesses", statistics.get("totalAccesses"));
            model.addAttribute("allowedAccesses", statistics.get("allowedAccesses"));
            model.addAttribute("deniedAccesses", statistics.get("deniedAccesses"));
            model.addAttribute("uniqueUsers", statistics.get("uniqueUsers"));
            model.addAttribute("todayAccesses", statistics.get("todayAccesses"));
            
            logger.info("Se cargaron {} accesos de {} totales", 
                       accessesPage.getContent().size(), 
                       accessesPage.getTotalElements());
            return "admin/accesses";
        } catch (Exception e) {
            logger.error("Error al cargar accesos: {}", e.getMessage(), e);
            model.addAttribute("error", "Error al cargar los accesos");
            return "admin/accesses";
        }
    }

    @GetMapping("/get/{id}")
    @ResponseBody
    public Access getAccessById(@PathVariable String id) {
        logger.info("Obteniendo acceso con ID: {}", id);
        return accessService.getAccessById(id);
    }

    @PostMapping("/update-result/{id}")
    public String updateAccessResult(
            @PathVariable String id,
            @RequestParam String result,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String resultFilter,
            @RequestParam(required = false) String search,
            RedirectAttributes redirectAttributes) {
        try {
            logger.info("Actualizando resultado del acceso {} a {}", id, result);
            accessService.updateAccessResult(id, result);
            redirectAttributes.addFlashAttribute("success", "Resultado del acceso actualizado exitosamente");
            logger.info("Resultado del acceso actualizado correctamente");
        } catch (Exception e) {
            logger.error("Error al actualizar resultado del acceso: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el resultado: " + e.getMessage());
        }
        
        return "redirect:/admin/accesses?page=" + page + 
               (resultFilter != null ? "&result=" + resultFilter : "") +
               (search != null ? "&search=" + search : "");
    }

    @PostMapping("/delete/{id}")
    public String deleteAccess(
            @PathVariable String id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String resultFilter,
            @RequestParam(required = false) String search,
            RedirectAttributes redirectAttributes) {
        try {
            logger.info("Eliminando acceso con ID: {}", id);
            accessService.deleteAccess(id);
            redirectAttributes.addFlashAttribute("success", "Acceso eliminado exitosamente");
            logger.info("Acceso eliminado con éxito");
        } catch (Exception e) {
            logger.error("Error al eliminar acceso: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el acceso: " + e.getMessage());
        }
        
        return "redirect:/admin/accesses?page=" + page + 
               (resultFilter != null ? "&result=" + resultFilter : "") +
               (search != null ? "&search=" + search : "");
    }

    @GetMapping("/statistics")
    @ResponseBody
    public Map<String, Object> getAccessStatistics() {
        logger.info("Obteniendo estadísticas de accesos");
        return accessService.getAccessStatistics();
    }

    @PostMapping("/create")
    @ResponseBody
    public Access createAccess(
            @RequestParam String userId,
            @RequestParam String qrCode,
            @RequestParam String result) {
        logger.info("Creando acceso para usuario: {}", userId);
        return accessService.createAccess(userId, qrCode, result);
    }
}