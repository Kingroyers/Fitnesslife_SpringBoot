package com.proaula.fitnesslife.service;

import com.proaula.fitnesslife.model.FunctionalTraining;
import com.proaula.fitnesslife.repository.FunctionalTrainingRepository;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
public class FunctionalTrainingService {

    private final FunctionalTrainingRepository repository;

    public FunctionalTrainingService(FunctionalTrainingRepository repository) {
        this.repository = repository;
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
        // Si quieres asegurar unicidad por idFunctionalTraining
        if(repository.existsByIdFunctionalTraining(training.getIdFunctionalTraining())) {
            throw new IllegalArgumentException("El idFunctionalTraining ya existe");
        }
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
}
