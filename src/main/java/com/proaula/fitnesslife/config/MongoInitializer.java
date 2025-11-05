package com.proaula.fitnesslife.config;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoInitializer {

    @Autowired
    private MongoTemplate mongoTemplate;

    @PostConstruct
    public void createCollectionsIfNotExist() {
        if (!mongoTemplate.collectionExists("FunctionalTraining")) {
            mongoTemplate.createCollection("FunctionalTraining");
            System.out.println("✅ Colección 'users' creada automáticamente");
        } else {
            System.out.println("ℹ️ Colección 'users' ya existe");
        }
    }
}
