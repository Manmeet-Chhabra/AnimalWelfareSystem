package com.manmeet.animalsys;

import org.springframework.boot.SpringApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableJpaRepositories
public class AnimalWelfareSysteem {

	public static void main(String[] args) {
		SpringApplication.run(AnimalWelfareSysteem.class, args);
	}

}
