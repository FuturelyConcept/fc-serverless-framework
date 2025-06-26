package com.fc.serverless.configsupplier;

import com.fc.serverless.sample.domain.PricingConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.function.Supplier;

/**
 * Updated ConfigSupplier Lambda - Now with LogFactory instead of System.out
 * Public service (no authentication required)
 *
 * Minimal changes: Only replaced System.out with LogFactory logging
 * Uses existing PricingConfig.defaultConfig() method - no changes to domain!
 */
public class ConfigSupplierFunction implements Supplier<PricingConfig> {

    private static final Log log = LogFactory.getLog(ConfigSupplierFunction.class);

    @Override
    public PricingConfig get() {
        log.info("⚙️ ConfigSupplier Lambda started!");
        log.info("📋 Providing current pricing configuration...");

        try {
            // In a real application, this would:
            // - Query a database
            // - Read from a configuration service
            // - Get settings from environment variables
            // - Call external pricing APIs

            // For demo purposes, we return the existing well-defined configuration
            PricingConfig config = PricingConfig.defaultConfig();

            log.info("✅ Configuration provided: " + config);
            return config;

        } catch (Exception e) {
            log.error("❌ Error providing configuration: " + e.getMessage(), e);
            throw new RuntimeException("Configuration retrieval failed: " + e.getMessage(), e);
        }
    }
}