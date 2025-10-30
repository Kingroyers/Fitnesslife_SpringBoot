package com.proaula.fitnesslife;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@SpringBootApplication
@EnableRedisHttpSession // <- hace que las sesiones HTTP se guarden en Redis
public class FitnesslifeApplication {

	public static void main(String[] args) {
		SpringApplication.run(FitnesslifeApplication.class, args);
	}

}
