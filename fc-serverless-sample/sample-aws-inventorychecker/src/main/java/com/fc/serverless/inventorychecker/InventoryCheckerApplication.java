package com.fc.serverless.inventorychecker;

import com.fc.serverless.sample.domain.InventoryCheckRequest;
import com.fc.serverless.sample.domain.InventoryResult;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.util.function.Function;

/**
 * 🚀 Dedicated InventoryChecker Lambda Application
 * ONLY contains InventoryCheckFunction - nothing else!
 */
@SpringBootApplication
@ComponentScan(basePackages = {
        "com.fc.serverless.inventorychecker",
        "com.fc.serverless.config"
})
public class InventoryCheckerApplication {

    public static void main(String[] args) {
        System.out.println("🚀 FC InventoryChecker Lambda starting...");
        SpringApplication.run(InventoryCheckerApplication.class, args);
    }

    // Explicitly define the function bean to avoid Spring Cloud Function conflicts
    @Bean("inventoryChecker")
    public Function<InventoryCheckRequest, InventoryResult> inventoryChecker() {
        return new InventoryCheckFunction();
    }
}