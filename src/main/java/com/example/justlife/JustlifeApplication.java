package com.example.justlife;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class JustlifeApplication {

	public static void main(String[] args) {
		SpringApplication.run(JustlifeApplication.class, args);
	}

}
