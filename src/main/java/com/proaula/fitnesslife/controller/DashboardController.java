package com.proaula.fitnesslife.controller;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DashboardController(
            FunctionalTrainingService service,
            UserService userService,
            RoleService roleService,
            PasswordEncoder passwordEncoder) {
        this.service = service;
        this.userService = userService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
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
    public String userTable(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            Model model) {

        model.addAttribute("currentPage", "usuarios");

        Page<User> usersPage = userService.getUsersPaginated(page, size, role, status, search);

        model.addAttribute("users", usersPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", usersPage.getTotalPages());
        model.addAttribute("totalItems", usersPage.getTotalElements());
        model.addAttribute("pageSize", size);
        model.addAttribute("roleFilter", role != null ? role : "");
        model.addAttribute("statusFilter", status != null ? status : "");
        model.addAttribute("searchTerm", search != null ? search : "");

        model.addAttribute("roles", roleService.findAll());
        model.addAttribute("newUser", new User());

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

    @PostMapping("/admin/users/create")
    public String createUser(
            @ModelAttribute User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String roleFilter,
            @RequestParam(required = false) String statusFilter,
            @RequestParam(required = false) String search,
            RedirectAttributes redirectAttributes) {
        try {
            if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "El email es requerido");
                return buildRedirectUrl("/admin/userTable", page, roleFilter, statusFilter, search);
            }

            if (user.getPassword() != null && !user.getPassword().trim().isEmpty()) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            } else {
                redirectAttributes.addFlashAttribute("error", "La contraseña es requerida");
                return buildRedirectUrl("/admin/userTable", page, roleFilter, statusFilter, search);
            }

            if (user.getRole() == null || user.getRole().trim().isEmpty()) {
                user.setRole("USER");
            }
            user.setActive(true);

            userService.createUser(user);
            redirectAttributes.addFlashAttribute("success", "Usuario creado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear usuario: " + e.getMessage());
        }

        return buildRedirectUrl("/admin/userTable", page, roleFilter, statusFilter, search);
    }

    @PostMapping("/admin/users/update/{id}")
    public String updateUser(
            @PathVariable String id,
            @ModelAttribute User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String roleFilter,
            @RequestParam(required = false) String statusFilter,
            @RequestParam(required = false) String search,
            RedirectAttributes redirectAttributes) {
        try {
            userService.updateUser(id, user);
            redirectAttributes.addFlashAttribute("success", "Usuario actualizado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar usuario: " + e.getMessage());
        }

        return buildRedirectUrl("/admin/userTable", page, roleFilter, statusFilter, search);
    }

    @PostMapping("/update-role")
    public String updateUserRole(
            @RequestParam String id,
            @RequestParam String role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String roleFilter,
            @RequestParam(required = false) String statusFilter,
            @RequestParam(required = false) String search,
            RedirectAttributes redirectAttributes) {
        try {
            userService.updateUserRole(id, role);
            redirectAttributes.addFlashAttribute("success", "Rol actualizado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el rol");
        }

        return buildRedirectUrl("/admin/userTable", page, roleFilter, statusFilter, search);
    }

    @PostMapping("/admin/users/delete/{id}")
    public String deleteUser(
            @PathVariable("id") String id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String roleFilter,
            @RequestParam(required = false) String statusFilter,
            @RequestParam(required = false) String search,
            RedirectAttributes redirectAttributes,
            @AuthenticationPrincipal UserDetails currentUser) {
        try {
            Optional<User> userOpt = userService.findById(id);

            if (userOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
                return buildRedirectUrl("/admin/userTable", page, roleFilter, statusFilter, search);
            }

            User user = userOpt.get();

            if (user.getEmail().equals(currentUser.getUsername())) {
                redirectAttributes.addFlashAttribute("error", "No puedes eliminarte a ti mismo");
                return buildRedirectUrl("/admin/userTable", page, roleFilter, statusFilter, search);
            }

            userService.deleteById(id);

            redirectAttributes.addFlashAttribute("success", "Usuario eliminado exitosamente");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el usuario: " + e.getMessage());
        }

        return buildRedirectUrl("/admin/userTable", page, roleFilter, statusFilter, search);
    }

    @GetMapping("/admin/users/get/{id}")
    @ResponseBody
    public User getUserById(@PathVariable String id) {
        return userService.getUserByIdOrThrow(id);
    }

    private String buildRedirectUrl(String basePath, int page, String roleFilter, String statusFilter, String search) {
        StringBuilder url = new StringBuilder("redirect:");
        url.append(basePath);
        url.append("?page=").append(page);

        if (roleFilter != null && !roleFilter.trim().isEmpty()) {
            url.append("&role=").append(roleFilter);
        }

        if (statusFilter != null && !statusFilter.trim().isEmpty()) {
            url.append("&status=").append(statusFilter);
        }

        if (search != null && !search.trim().isEmpty()) {
            url.append("&search=").append(search);
        }

        return url.toString();
    }
}