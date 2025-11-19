package com.proaula.fitnesslife.repository;

import com.proaula.fitnesslife.model.FunctionalTraining;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FunctionalTrainingRepository extends MongoRepository<FunctionalTraining, String> {
    
    Optional<FunctionalTraining> findByIdFunctionalTraining(String id);
    Optional<FunctionalTraining> findByIdFunctionalTraining(int idFunctionalTraining);
    void deleteByIdFunctionalTraining(int idFunctionalTraining);
    

    
    boolean existsByIdFunctionalTraining(int idFunctionalTraining);
}