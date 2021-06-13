package com.example.apidesafio;

import com.example.apidesafio.service.DBHandler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ApidesafioApplication {

	public static void main(String[] args) {
		DBHandler.connect();
		DBHandler.createCertTable();
		SpringApplication.run(ApidesafioApplication.class, args);
	}

}
