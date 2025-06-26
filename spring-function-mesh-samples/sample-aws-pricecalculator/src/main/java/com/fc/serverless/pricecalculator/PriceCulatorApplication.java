package com.fc.serverless.pricecalculator;

import com.fc.serverless.sample.domain.OrderRequest;
import com.fc.serverless.sample.domain.PriceInfo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.function.Function;

/**
 * 🚀 Dedicated PriceCulatorApplication Lambda Application
 * ONLY contains PriceCulatorApplication - nothing else!
 */
@SpringBootApplication
public class PriceCulatorApplication {

    public static void main(String[] args) {
        System.out.println("🚀 FC PriceCulatorApplication Lambda starting...");
        SpringApplication.run(PriceCulatorApplication.class, args);
    }

    // Explicitly define the function bean to avoid Spring Cloud Function conflicts
    @Bean("priceCalculator")
    public Function<OrderRequest, PriceInfo> paymentProcessor() {
        return new PriceCalculatorFunction();
    }
}