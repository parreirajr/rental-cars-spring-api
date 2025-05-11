package com.challenge.rental_cars_spring_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class RentalCarsSpringApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(RentalCarsSpringApiApplication.class, args);
    }

}
