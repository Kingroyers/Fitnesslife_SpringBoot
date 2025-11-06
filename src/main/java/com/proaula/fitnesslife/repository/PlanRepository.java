package com.proaula.fitnesslife.repository;

import com.proaula.fitnesslife.model.Plan;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanRepository extends MongoRepository<Plan, String> {
    List<Plan> findAllByOrderByPriceAsc();
}
