package com.korea.basic1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GongOhHaApplication {

	public static void main(String[] args) {
		SpringApplication.run(GongOhHaApplication.class, args);
	}

}
