package com.proaula.fitnesslife.controller;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.proaula.fitnesslife.model.FunctionalTraining;
import com.proaula.fitnesslife.model.User;
import com.proaula.fitnesslife.service.FunctionalTrainingService;
import com.proaula.fitnesslife.service.RoleService;
import com.proaula.fitnesslife.service.UserService;

@Controller
public class DashboardController {

    private final FunctionalTrainingService service;
    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public DashboardController(FunctionalTrainingService service, UserService userService,
            RoleService roleService) {
        this.service = service;
        this.userService = userService;
        this.roleService = roleService;
    }

    private static final String VIEW_DASHBOARD = "admin/dashboard";
    private static final String VIEW_USER = "admin/userTable";
    private static final String VIEW_FUNCTIONALTRAINING = "admin/functionalTrainingCrud";

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("currentPage", "dashboard");
        return VIEW_DASHBOARD;
    }

    @GetMapping("/admin/userTable")
    public String userTable(Model model) {

        model.addAttribute("currentPage", "usuarios");
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("roles", roleService.findAll());

        return VIEW_USER;
    }

    @GetMapping("/admin/functionalTraining")
    public String functionalTraining(Model model) {
        List<FunctionalTraining> trainings = service.getAllTrainings();
        LocalDate hoy = LocalDate.now(ZoneId.systemDefault());

        List<FunctionalTraining> clasesDeHoy = trainings.stream()
                .filter(t -> t.getDatetime() != null)
                .filter(t -> {
                    LocalDate fechaClase = t.getDatetime().toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();
                    return fechaClase.isEqual(hoy);
                })
                .sorted(Comparator.comparing(FunctionalTraining::getDatetime))
                .toList();

        model.addAttribute("currentPage", "clases");
        model.addAttribute("clases", clasesDeHoy);
        model.addAttribute("trainings", trainings);
        model.addAttribute("training", new FunctionalTraining());

        return VIEW_FUNCTIONALTRAINING;
    }

    @PostMapping("/update-role")
    public String updateUserRole(@RequestParam String id,
            @RequestParam String role,
            RedirectAttributes redirectAttributes) {
        try {
            userService.updateUserRole(id, role);
            redirectAttributes.addFlashAttribute("success", "Rol actualizado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el rol");
        }
        return "redirect:/admin/userTable";
    }

    @PostMapping("/admin/users/delete/{id}")
    public String deleteUser(@PathVariable("id") String id,
            RedirectAttributes redirectAttributes,
            @AuthenticationPrincipal UserDetails currentUser) {
        try {
            Optional<User> userOpt = userService.findById(id);

            if (userOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
                return "redirect:/admin/userTable";
            }

            User user = userOpt.get();

            // Verificar que no se esté eliminando a sí mismo
            if (user.getEmail().equals(currentUser.getUsername())) {
                redirectAttributes.addFlashAttribute("error", "No puedes eliminarte a ti mismo");
                return "redirect:/admin/userTable";
            }

            // Eliminar el usuario
            userService.deleteById(id);

            redirectAttributes.addFlashAttribute("success", "Usuario eliminado exitosamente");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el usuario: " + e.getMessage());
        }

        return "redirect:/admin/userTable";
    }
}
