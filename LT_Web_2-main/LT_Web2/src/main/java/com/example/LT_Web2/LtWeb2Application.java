package com.example.LT_Web2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LtWeb2Application {

	public static void main(String[] args) {
		SpringApplication.run(LtWeb2Application.class, args);
	}

}
