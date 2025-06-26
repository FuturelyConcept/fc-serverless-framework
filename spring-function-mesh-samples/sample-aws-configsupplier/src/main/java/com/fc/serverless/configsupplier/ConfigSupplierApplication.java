package com.fc.serverless.configsupplier;

import com.fc.serverless.sample.domain.PricingConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.function.Supplier;

/**
 * 🚀 Dedicated ConfigSupplierApplication Lambda Application
 * ONLY contains ConfigSupplierApplication - nothing else!
 */
@SpringBootApplication
public class ConfigSupplierApplication {

    public static void main(String[] args) {
        System.out.println("🚀 FC ConfigSupplierApplication Lambda starting...");
        SpringApplication.run(ConfigSupplierApplication.class, args);
    }

    // Explicitly define the function bean to avoid Spring Cloud Function conflicts
    @Bean("configSupplier")
    public Supplier<PricingConfig> configSupplier() {
        return new ConfigSupplierFunction();
    }
}