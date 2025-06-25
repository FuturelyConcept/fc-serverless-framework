package com.fc.serverless.orderprocessor;

import com.fc.serverless.sample.domain.OrderRequest;
import com.fc.serverless.sample.domain.OrderResult;
import com.fc.serverless.sample.domain.PriceInfo;
import com.fc.serverless.core.annotation.RemoteFunction;

import java.util.function.Function;

/**
 * Lambda 1: OrderProcessor
 *
 * This is the main orchestrator that receives order requests and calls
 * the PriceCalculator to get pricing information.
 *
 * Demonstrates: Function<OrderRequest, PriceInfo> remote call
 */
public class OrderProcessorFunction implements Function<OrderRequest, OrderResult> {

    // FC Framework automatically creates an HTTP proxy for this!
    @RemoteFunction(name = "priceCalculator")
    private Function<OrderRequest, PriceInfo> priceCalculator;

    @Override
    public OrderResult apply(OrderRequest request) {
        System.out.println("🚀 OrderProcessor Lambda started!");
        System.out.println("📋 Processing order: " + request);

        try {
            // Validate input
            if (request == null || request.getProductId() == null || request.getQuantity() <= 0) {
                System.out.println("❌ Invalid order request");
                return OrderResult.failed("Invalid order request");
            }

            // Call PriceCalculator Lambda via FC Framework proxy
            // 🌐 This becomes an HTTP call to PriceCalculator Lambda!
            System.out.println("💰 Calling PriceCalculator Lambda...");
            PriceInfo priceInfo = priceCalculator.apply(request);

            if (priceInfo == null || priceInfo.getTotalPrice() == null) {
                System.out.println("❌ Price calculation failed");
                return OrderResult.failed("Price calculation failed");
            }

            System.out.println("✅ Price calculated: " + priceInfo);

            // Generate order ID and create successful result
            String orderId = "order-" + System.currentTimeMillis();
            String message = String.format("Order processed successfully! %s Total: $%.2f",
                    priceInfo.getDiscountReason(),
                    priceInfo.getTotalPrice());

            System.out.println("🎉 Order completed: " + orderId);
            return OrderResult.success(orderId, priceInfo.getTotalPrice(), message);

        } catch (Exception e) {
            System.err.println("❌ Error processing order: " + e.getMessage());
            e.printStackTrace();
            return OrderResult.failed("Error processing order: " + e.getMessage());
        }
    }
}