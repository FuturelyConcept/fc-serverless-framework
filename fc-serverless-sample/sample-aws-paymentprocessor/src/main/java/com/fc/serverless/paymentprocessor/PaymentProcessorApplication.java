package com.fdd.lambda.paymentprocessor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.util.function.Supplier;
/**
 * 🚀 Dedicated PaymentProcessorApplication Lambda Application
 * ONLY contains PaymentProcessorApplication - nothing else!
 */
@SpringBootApplication
public class PaymentProcessorApplication {
    public static void main(String[] args) {
        System.out.println("🚀 FDD PaymentProcessorApplication Lambda starting...");
        SpringApplication.run(PaymentProcessorApplication.class, args);
    }

    @Bean("sup")
    public Supplier<String> sup() {
        return () -> "Whats up";
    }
}