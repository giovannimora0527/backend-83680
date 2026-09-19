package com.uniminuto.clinica;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
/** Punto de entrada de la aplicación Spring Boot. */
public class ClinicaApplication {

    /** Inicia el servidor y el contexto de Spring. */
	public static void main(String[] args) {
		SpringApplication.run(ClinicaApplication.class, args);
	}

}
