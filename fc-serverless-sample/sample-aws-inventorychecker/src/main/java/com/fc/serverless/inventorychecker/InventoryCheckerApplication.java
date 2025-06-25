package com.fdd.lambda.inventorychecker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.util.function.Supplier;
/**
 * 🚀 Dedicated InventoryCheckerApplication Lambda Application
 * ONLY contains InventoryCheckerApplication - nothing else!
 */
@SpringBootApplication
public class InventoryCheckerApplication {
    public static void main(String[] args) {
        System.out.println("🚀 FDD InventoryCheckerApplication Lambda starting...");
        SpringApplication.run(InventoryCheckerApplication.class, args);
    }

    @Bean("sup")
    public Supplier<String> sup() {
        return () -> "Whats up";
    }
}