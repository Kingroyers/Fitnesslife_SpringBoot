package com.proaula.fitnesslife.controller;

import com.proaula.fitnesslife.service.QrCodeService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import com.proaula.fitnesslife.model.FunctionalTraining;
import com.proaula.fitnesslife.model.User;
import com.proaula.fitnesslife.repository.FunctionalTrainingRepository;
import com.proaula.fitnesslife.repository.UserRepository;
import com.proaula.fitnesslife.service.FunctionalTrainingService;

@Controller
public class UserController {

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private QrCodeService qrCodeService;

    @Autowired
    private FunctionalTrainingRepository trainingRepo;

    @Autowired
    private FunctionalTrainingService functionalTrainingService;

    public UserController(UserRepository userRepo, PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.encoder = encoder;
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user, Model model) {

        if (userRepo.findByEmail(user.getEmail()).isPresent()) {
            model.addAttribute("error", "El usuario ya existe");
            return "register";
        }

        user.setPassword(encoder.encode(user.getPassword()));
        user.setRole("USER");
        user.setActive(true);

        user.setSex("");
        user.setBirthDate(null);
        user.setBloodType("");
        user.setPhotoProfile("");
        user.setPlan("");
        user.setQrCodePath("");
        user.setLastLogin(null);

        userRepo.save(user);

        return "redirect:/login?registered";
    }

    @PostMapping("/inscribirme/{idFunctionalTraining}") // inscribirme a una clase
    public String inscribirme(@PathVariable int idFunctionalTraining, Principal principal) {

        User user = userRepo.findByEmail(principal.getName()).orElseThrow();

        FunctionalTraining training = trainingRepo.findByIdFunctionalTraining(idFunctionalTraining).orElseThrow();

        if (training.getUserIds().contains(user.getIdentification())) {
            return "redirect:/home?error=Ya inscrito";
        }

        training.getUserIds().add(user.getIdentification());
        trainingRepo.save(training);

        return "redirect:/home?success=Inscrito";
    }

    @PostMapping("/cancelarInscripcion/{id}")
    public String cancelarInscripcion(@PathVariable int id, Principal principal) {
        functionalTrainingService.cancelarInscripcion(id, principal.getName());
        return "redirect:/home?success=Cancelada";
    }

    @PostMapping("/actualizarUserPerfil")
    public String actualizarPerfil(@ModelAttribute User userForm, Principal principal) {
        User user = userRepo.findByEmail(principal.getName()).orElseThrow();

        // Solo actualizamos los campos que se pueden editar
        user.setName(userForm.getName());
        user.setLastname(userForm.getLastname());
        user.setPhone(userForm.getPhone());
        user.setSex(userForm.getSex());
        user.setBirthDate(userForm.getBirthDate());
        user.setBloodType(userForm.getBloodType());

        userRepo.save(user);
        return "redirect:/user-profile";
    }

}
