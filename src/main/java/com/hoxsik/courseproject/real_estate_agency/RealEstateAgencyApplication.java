package com.hoxsik.courseproject.real_estate_agency;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600)
public class RealEstateAgencyApplication {
	public static void main(String[] args) {
		SpringApplication.run(RealEstateAgencyApplication.class, args);
	}
}
