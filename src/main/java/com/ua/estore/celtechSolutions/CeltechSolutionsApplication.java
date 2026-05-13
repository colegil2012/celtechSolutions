package com.ua.estore.celtechSolutions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class CeltechSolutionsApplication {

	public static void main(String[] args) {
		SpringApplication.run(CeltechSolutionsApplication.class, args);
	}

}
