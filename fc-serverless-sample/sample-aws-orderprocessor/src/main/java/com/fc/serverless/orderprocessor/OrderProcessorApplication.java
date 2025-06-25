package com.fc.serverless.orderprocessor;

import com.fc.serverless.sample.domain.CreateOrderRequest;
import com.fc.serverless.sample.domain.OrderResult;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.util.function.Function;

/**
 * 🚀 Dedicated OrderProcessor Lambda Application
 * ONLY contains OrderProcessorFunction - nothing else!
 */
@SpringBootApplication
@ComponentScan(basePackages = {
        "com.fc.serverless.orderprocessor",
        "com.fc.serverless.config"
})
public class OrderProcessorApplication {

    public static void main(String[] args) {
        System.out.println("🚀 FC OrderProcessor Lambda starting...");
        SpringApplication.run(OrderProcessorApplication.class, args);
    }

    // Explicitly define the function bean to avoid Spring Cloud Function conflicts
    @Bean("orderProcessor")
    public Function<CreateOrderRequest, OrderResult> orderProcessor() {
        return new OrderProcessorFunction();
    }
}