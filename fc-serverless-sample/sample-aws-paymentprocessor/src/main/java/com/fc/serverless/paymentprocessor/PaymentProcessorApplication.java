package com.fc.serverless.paymentprocessor;

import com.fc.serverless.sample.domain.PaymentRequest;
import com.fc.serverless.sample.domain.PaymentResult;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.util.function.Function;

/**
 * 🚀 Dedicated PaymentProcessor Lambda Application
 * ONLY contains PaymentProcessorFunction - nothing else!
 */
@SpringBootApplication
@ComponentScan(basePackages = {
        "com.fc.serverless.paymentprocessor",
        "com.fc.serverless.config"
})
public class PaymentProcessorApplication {

    public static void main(String[] args) {
        System.out.println("🚀 FC PaymentProcessor Lambda starting...");
        SpringApplication.run(PaymentProcessorApplication.class, args);
    }

    // Explicitly define the function bean to avoid Spring Cloud Function conflicts
    @Bean("paymentProcessor")
    public Function<PaymentRequest, PaymentResult> paymentProcessor() {
        return new PaymentProcessorFunction();
    }
}