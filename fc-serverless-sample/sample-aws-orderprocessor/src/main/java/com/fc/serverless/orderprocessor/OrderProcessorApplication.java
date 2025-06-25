package com.fc.serverless.orderprocessor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.function.Supplier;
/**
 * 🚀 Dedicated UserValidator Lambda Application
 * ONLY contains UserValidationFunction - nothing else!
 */
@SpringBootApplication
public class OrderProcessorApplication {
    public static void main(String[] args) {
        System.out.println("🚀 FDD OrderProcessorApplication Lambda starting...");
        SpringApplication.run(OrderProcessorApplication.class, args);
    }

    @Bean("sup")
    public Supplier<String> sup() {
        return () -> "Whats up";
    }
}