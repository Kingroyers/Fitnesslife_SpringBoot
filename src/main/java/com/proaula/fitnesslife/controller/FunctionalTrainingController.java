package com.proaula.fitnesslife.controller;

import com.proaula.fitnesslife.model.FunctionalTraining;
import com.proaula.fitnesslife.service.FunctionalTrainingService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/trainings")
public class FunctionalTrainingController {

    private final FunctionalTrainingService service;

    public FunctionalTrainingController(FunctionalTrainingService service) {
        this.service = service;
    }

    @GetMapping
    public String listTrainings(Model model) {
        List<FunctionalTraining> trainings = service.getAllTrainings();
        model.addAttribute("training", trainings);

        return "admin/functionalTrainingCrud"; 
    }

    // crear nuevo entrenamiento
    @PostMapping("/save")
    public String saveTraining(@ModelAttribute FunctionalTraining training) {
        service.createTraining(training);

        return "redirect:/admin/functionalTraining";
    }

    @PostMapping("/delete/{id}")
    public String deleteTraining(@PathVariable String id) {
        service.deleteTraining(id);

        return "redirect:/admin/functionalTraining?deleted";
    }

    // CONSULTA JSON OPCIONAL (por si lo necesitas en JavaScript)
    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<FunctionalTraining> getTrainingById(@PathVariable String id) {
        return service.getTrainingById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ACTUALIZAR ENTRENAMIENTO
    @PostMapping("/update")
    public String updateTrainingPost(
            @RequestParam("id") String id,
            @RequestParam("nameTraining") String nameTraining,
            @RequestParam("instructor") String instructor,
            @RequestParam("room") String room,
            @RequestParam("date") String date,
            @RequestParam("time") String time) {

        FunctionalTraining existing = service.getTrainingById(id)
                .orElseThrow(() -> new IllegalArgumentException("Entrenamiento no encontrado"));

        existing.setNameTraining(nameTraining);
        existing.setInstructor(instructor);
        existing.setRoom(room);

        if (!date.isEmpty() && !time.isEmpty()) {
            String datetimeStr = date + " " + time;
            try {
                java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
                existing.setDatetime(formatter.parse(datetimeStr));
            } catch (java.text.ParseException e) {
                throw new IllegalArgumentException("Fecha u hora inválida");
            }
        }

        service.updateTraining(existing.getId(), existing);

        return "redirect:/admin/functionalTraining";
    }

}
