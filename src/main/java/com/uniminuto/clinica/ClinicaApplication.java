package com.uniminuto.clinica;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal que arranca la aplicación de la clínica veterinaria.
 */
@SpringBootApplication
public class ClinicaApplication {

	/**
	 * Punto de entrada de la aplicación.
	 *
	 * @param args argumentos de la línea de comandos.
	 */
	public static void main(String[] args) {
		SpringApplication.run(ClinicaApplication.class, args);
	}

}
