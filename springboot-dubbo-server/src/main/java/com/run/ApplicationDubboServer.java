package com.run;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(
	basePackages={
		"com.tdu.service",
		"com.tdu.web"
	}
)
public class ApplicationDubboServer {

	public static void main(String[] args) {
		SpringApplication.run(ApplicationDubboServer.class, args);
	}
}
