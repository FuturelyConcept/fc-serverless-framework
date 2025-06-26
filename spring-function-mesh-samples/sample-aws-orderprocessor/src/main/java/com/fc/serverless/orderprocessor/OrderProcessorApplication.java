package com.fc.serverless.orderprocessor;

import com.fc.serverless.sample.domain.OrderRequest;
import com.fc.serverless.sample.domain.OrderResult;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.function.Function;

/**
 * 🚀 Dedicated OrderProcessor Lambda Application
 * ONLY contains OrderProcessorFunction - nothing else!
 */
@SpringBootApplication
public class OrderProcessorApplication {

    public static void main(String[] args) {
        System.out.println("🚀 FC OrderProcessor Lambda starting...");
        SpringApplication.run(OrderProcessorApplication.class, args);
    }

    // Explicitly define the function bean to avoid Spring Cloud Function conflicts
    @Bean("orderProcessor")
    public Function<OrderRequest, OrderResult> orderProcessor() {
        return new OrderProcessorFunction();
    }
}