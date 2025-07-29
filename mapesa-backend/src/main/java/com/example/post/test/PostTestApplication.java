package com.example.post.test;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Smart Fire Backend API", version = "1.0", description = "API for the Smart Fire Backend application"))
public class PostTestApplication {

	public static void main(String[] args) {
		SpringApplication.run(PostTestApplication.class, args);
	}

}
