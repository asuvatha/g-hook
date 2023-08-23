package com.yazata.tar.ghook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan("com.yazata.tar")
@EnableScheduling
public class GHookApplication {

	public static void main(String[] args) {
		SpringApplication.run(GHookApplication.class, args);
	}

}
