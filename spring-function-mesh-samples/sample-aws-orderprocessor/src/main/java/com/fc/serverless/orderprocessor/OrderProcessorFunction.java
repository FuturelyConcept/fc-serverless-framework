package com.fc.serverless.orderprocessor;

import com.fc.serverless.sample.domain.OrderRequest;
import com.fc.serverless.sample.domain.OrderResult;
import com.fc.serverless.sample.domain.PriceInfo;
import com.fc.serverless.core.annotation.RemoteFunction;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.function.Function;

/**
 * Updated OrderProcessor Lambda - Now with proper logging and calls PriceCalculator (IAM-protected)
 *
 * This is the main orchestrator that receives order requests and calls
 * the PriceCalculator to get pricing information.
 *
 * Demonstrates: Function<OrderRequest, PriceInfo> remote call with IAM auth
 */
public class OrderProcessorFunction implements Function<OrderRequest, OrderResult> {

    private static final Log log = LogFactory.getLog(OrderProcessorFunction.class);

    // FC Framework automatically creates an HTTP proxy for this with IAM auth!
    @RemoteFunction(name = "priceCalculator")
    private Function<OrderRequest, PriceInfo> priceCalculator;

    @Override
    public OrderResult apply(OrderRequest request) {
        log.info("🚀 OrderProcessor Lambda started!");
        log.info("📋 Processing order: " + request);

        try {
            // Validate input
            if (request == null || request.getProductId() == null || request.getQuantity() <= 0) {
                log.error("❌ Invalid order request");
                return OrderResult.failed("Invalid order request");
            }

            // Call PriceCalculator Lambda via FC Framework proxy with IAM authentication
            // 🌐 This becomes an HTTP call to PriceCalculator Lambda with AWS SigV4 signing!
            log.info("💰 Calling PriceCalculator Lambda (🔐 IAM-protected)...");
            PriceInfo priceInfo = priceCalculator.apply(request);

            if (priceInfo == null || priceInfo.getTotalPrice() == null) {
                log.error("❌ Price calculation failed");
                return OrderResult.failed("Price calculation failed");
            }

            log.info("✅ Price calculated: " + priceInfo);

            // Generate order ID and create successful result
            String orderId = "order-" + System.currentTimeMillis();
            String message = String.format("Order processed successfully! %s Total: $%.2f",
                    priceInfo.getDiscountReason(),
                    priceInfo.getTotalPrice());

            log.info("🎉 Order completed: " + orderId);
            return OrderResult.success(orderId, priceInfo.getTotalPrice(), message);

        } catch (Exception e) {
            log.error("❌ Error processing order: " + e.getMessage(), e);
            return OrderResult.failed("Error processing order: " + e.getMessage());
        }
    }
}