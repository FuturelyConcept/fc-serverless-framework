package com.fc.serverless.orderprocessor;

import com.fc.serverless.sample.domain.*;
import com.fc.serverless.core.annotation.RemoteFunction;

import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.function.Function;

/**
 * This demonstrates the CORE FC concept:
 * - Developer writes pure business logic with @RemoteFunction functions
 * - Since other functions DON'T EXIST locally, FC creates HTTP proxies automatically!
 * - Same code works locally AND in distributed Lambda environment
 * - NO knowledge of Lambda URLs, HTTP calls, or AWS APIs required!
 */
public class OrderProcessorFunction implements Function<CreateOrderRequest, OrderResult> {

    // FC framework will automatically create HTTP proxies to call the other Lambdas!

    @RemoteFunction(name = "userValidator")
    private Function<UserData, ValidationResult> userValidator;

    @RemoteFunction(name = "inventoryChecker")
    private Function<InventoryCheckRequest, InventoryResult> inventoryChecker;

    @RemoteFunction(name = "paymentProcessor")
    private Function<PaymentRequest, PaymentResult> paymentProcessor;

    @Override
    public OrderResult apply(CreateOrderRequest request) {
        System.out.println("🚀 DEDICATED OrderProcessor Lambda executing!");
        System.out.println("🛒 Processing order for: " + (request != null && request.getUserData() != null ? request.getUserData().getName() : "null"));

        try {
            // Validate input
            if (request == null) {
                System.out.println("❌ Order request is null");
                return OrderResult.failed("Order request is null");
            }

            if (request.getUserData() == null) {
                System.out.println("❌ User data is null");
                return OrderResult.failed("User data is required");
            }

            // Step 1: Validate user data
            // 🌐 This will be an HTTP call to UserValidator Lambda!
            System.out.println("👤 Calling UserValidator Lambda via FC proxy...");
            ValidationResult validation = userValidator.apply(request.getUserData());
            if (!validation.isValid()) {
                System.out.println("❌ User validation failed: " + validation.getMessage());
                return OrderResult.failed("User validation failed: " + validation.getMessage());
            }
            System.out.println("✅ User validation passed via cross-Lambda call");

            // Step 2: Check inventory availability
            // 🌐 This will be an HTTP call to InventoryChecker Lambda!
            System.out.println("📦 Calling InventoryChecker Lambda via FC proxy...");
            InventoryCheckRequest inventoryRequest = new InventoryCheckRequest(
                    request.getProductId(),
                    request.getQuantity()
            );
            InventoryResult inventoryResult = inventoryChecker.apply(inventoryRequest);
            if (!inventoryResult.isAvailable()) {
                System.out.println("❌ Inventory check failed: " + inventoryResult.getMessage());
                return OrderResult.failed("Inventory check failed: " + inventoryResult.getMessage());
            }
            System.out.println("✅ Inventory check passed via cross-Lambda call");

            // Step 3: Process payment
            // 🌐 This will be an HTTP call to PaymentProcessor Lambda!
            System.out.println("💳 Calling PaymentProcessor Lambda via FC proxy...");
            PaymentRequest paymentRequest = new PaymentRequest(
                    request.getUserData().getName(),
                    calculateOrderTotal(request),
                    "USD",
                    request.getPaymentMethod() != null ? request.getPaymentMethod() : "CARD",
                    "temp-order-id"
            );

            PaymentResult paymentResult = paymentProcessor.apply(paymentRequest);
            if (!paymentResult.isSuccess()) {
                System.out.println("❌ Payment failed: " + paymentResult.getMessage());
                return OrderResult.failed("Payment processing failed: " + paymentResult.getMessage());
            }
            System.out.println("✅ Payment processed via cross-Lambda call");

            // Step 4: Create order
            String orderId = "fc-cross-lambda-order-" + System.currentTimeMillis();
            System.out.println("🎉 Order created successfully: " + orderId);
            System.out.println("🚀 All function calls were made across Lambda boundaries!");

            return OrderResult.success(orderId, paymentResult.getTransactionId());

        } catch (Exception e) {
            System.err.println("❌ Order processing failed: " + e.getMessage());
            e.printStackTrace();
            return OrderResult.failed("Order processing error: " + e.getMessage());
        }
    }

    private BigDecimal calculateOrderTotal(CreateOrderRequest request) {
        BigDecimal unitPrice = new BigDecimal("10.00");
        return unitPrice.multiply(new BigDecimal(request.getQuantity()));
    }
}