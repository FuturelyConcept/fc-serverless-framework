package com.fc.serverless.inventorychecker;

import com.fc.serverless.sample.domain.InventoryCheckRequest;
import com.fc.serverless.sample.domain.InventoryResult;

import java.util.function.Function;

public class InventoryCheckFunction implements Function<InventoryCheckRequest, InventoryResult> {

    @Override
    public InventoryResult apply(InventoryCheckRequest request) {
        System.out.println("🚀 DEDICATED InventoryChecker Lambda executing!");
        System.out.println("📦 Checking inventory for: " + (request != null ? request.getProductId() : "null"));

        if (request == null) {
            System.out.println("❌ Request is null");
            return InventoryResult.unavailable("Request is null");
        }

        if (request.getProductId() == null || request.getProductId().trim().isEmpty()) {
            System.out.println("❌ Product ID is required");
            return InventoryResult.unavailable("Product ID is required");
        }

        if (request.getQuantity() <= 0) {
            System.out.println("❌ Invalid quantity: " + request.getQuantity());
            return InventoryResult.unavailable("Quantity must be positive");
        }

        // Simple business logic - max 100 units available
        boolean available = request.getQuantity() <= 100;

        if (available) {
            System.out.println("✅ Inventory available for: " + request.getProductId() + " qty:" + request.getQuantity());
            return InventoryResult.available(request.getQuantity());
        } else {
            System.out.println("❌ Insufficient inventory for: " + request.getProductId() + " qty:" + request.getQuantity());
            return InventoryResult.unavailable("Insufficient inventory. Max 100 units available, requested: " + request.getQuantity());
        }
    }
}