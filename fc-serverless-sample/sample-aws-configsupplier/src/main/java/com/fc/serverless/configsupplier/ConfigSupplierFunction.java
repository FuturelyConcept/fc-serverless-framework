package com.fc.serverless.configsupplier;

import com.fc.serverless.sample.domain.PricingConfig;

import java.util.function.Supplier;

/**
 * Lambda 3: ConfigSupplier
 *
 * Provides pricing configuration to other services.
 * This demonstrates the Supplier<T> interface - no input parameters,
 * just returns configuration data.
 *
 * Demonstrates: Supplier<PricingConfig> implementation
 */
public class ConfigSupplierFunction implements Supplier<PricingConfig> {

    @Override
    public PricingConfig get() {
        System.out.println("⚙️ ConfigSupplier Lambda started!");
        System.out.println("📋 Providing current pricing configuration...");

        try {
            // In a real application, this would:
            // - Query a database
            // - Read from a configuration service
            // - Get settings from environment variables
            // - Call external pricing APIs

            // For demo purposes, we return a well-defined configuration
            PricingConfig config = PricingConfig.defaultConfig();

            System.out.println("✅ Configuration provided: " + config);
            return config;

        } catch (Exception e) {
            System.err.println("❌ Error providing configuration: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Configuration retrieval failed: " + e.getMessage());
        }
    }
}