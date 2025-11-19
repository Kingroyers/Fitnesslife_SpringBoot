package com.proaula.fitnesslife.controller;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

}
