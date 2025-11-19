package com.proaula.fitnesslife.service;

import com.proaula.fitnesslife.model.FunctionalTraining;
import com.proaula.fitnesslife.model.User;
import com.proaula.fitnesslife.repository.FunctionalTrainingRepository;
import com.proaula.fitnesslife.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Service
public class FunctionalTrainingService {

    private final FunctionalTrainingRepository repository;
    private final UserRepository userRepository;

    public FunctionalTrainingService(FunctionalTrainingRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    public List<FunctionalTraining> getAllTrainings() {
        return repository.findAll();
    }

    public Optional<FunctionalTraining> getTrainingById(String id) {
        return repository.findById(id);
    }

    public Optional<FunctionalTraining> getTrainingByFunctionalId(int idFunctionalTraining) {
        return repository.findByIdFunctionalTraining(idFunctionalTraining);
    }

    public FunctionalTraining createTraining(FunctionalTraining training) {

        long count = repository.count();
        training.setIdFunctionalTraining((int) count + 1);
        training.setStatus("Active");
        // if
        // (repository.existsByIdFunctionalTraining(training.getIdFunctionalTraining()))
        // {
        // throw new IllegalArgumentException("El idFunctionalTraining ya existe");
        // }
        return repository.save(training);
    }

    public FunctionalTraining updateTraining(String id, FunctionalTraining updatedTraining) {
        return repository.findById(id).map(training -> {
            training.setNameTraining(updatedTraining.getNameTraining());
            training.setInstructor(updatedTraining.getInstructor());
            training.setDescription(updatedTraining.getDescription());
            training.setMaximumCapacity(updatedTraining.getMaximumCapacity());
            training.setDuration(updatedTraining.getDuration());
            training.setStatus(updatedTraining.getStatus());
            training.setDatetime(updatedTraining.getDatetime());
            training.setRoom(updatedTraining.getRoom());
            return repository.save(training);
        }).orElseThrow(() -> new IllegalArgumentException("Training not found"));
    }

    public void deleteTraining(String id) {
        repository.deleteById(id);
    }

    // cancelar inscripción
    public void cancelarInscripcion(int idFunctionalTraining, String emailUsuario) {
        User user = userRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        FunctionalTraining training = repository.findByIdFunctionalTraining(idFunctionalTraining)
                .orElseThrow(() -> new RuntimeException("Clase no encontrada"));

        if (training.getUserIds() != null && training.getUserIds().contains(user.getIdentification())) {
            training.getUserIds().remove(user.getIdentification());
            repository.save(training);
        }
    }

    public FunctionalTraining findByIdFunctional(int idFunctional) {
        return repository.findByIdFunctionalTraining(idFunctional).orElse(null);
    }

    // Nuevo método: obtener solo las clases donde el usuario está inscrito
    public List<FunctionalTraining> getTrainingsByUser(String emailUsuario) {
        User user = userRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<FunctionalTraining> allTrainings = repository.findAll();

        // Filtrar solo las clases donde el usuario esté inscrito
        return allTrainings.stream()
                .filter(training -> training.getUserIds() != null &&
                        training.getUserIds().contains(user.getIdentification()))
                .toList();
    }

    public void actualizarEstados() {
        List<FunctionalTraining> clases = repository.findAll();
        LocalDateTime ahora = LocalDateTime.now();

        clases.forEach(c -> {
            LocalDateTime fechaClase = c.getDatetime()
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            if (fechaClase.isBefore(ahora) && !"Inactive".equals(c.getStatus())) {
                c.setStatus("Inactive");
                repository.save(c);
            }
        });
    }

}