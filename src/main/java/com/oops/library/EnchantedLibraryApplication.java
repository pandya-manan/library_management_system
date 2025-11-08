package com.oops.library;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EnchantedLibraryApplication {

	public static void main(String[] args) {
		SpringApplication.run(EnchantedLibraryApplication.class, args);
	}

}
