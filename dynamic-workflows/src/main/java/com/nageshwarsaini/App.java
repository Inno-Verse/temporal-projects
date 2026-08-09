package com.nageshwarsaini;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for bootstrapping the Spring Boot application.
 *
 * @author nageshwarsaini
 */
@SpringBootApplication
public class App {

    /**
     * Entry point of the Spring Boot application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}