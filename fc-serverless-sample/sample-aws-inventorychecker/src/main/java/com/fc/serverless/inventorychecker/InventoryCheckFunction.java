package com.fc.serverless.inventorychecker;

import com.fc.serverless.sample.domain.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.function.Function;

@Component("inventoryChecker")
public class InventoryCheckFunction implements Function<InventoryCheckRequest, InventoryResult> {

    @Override
    public InventoryResult apply(InventoryCheckRequest request) {
        System.out.println("🚀 DEDICATED InventoryChecker Lambda executing!");
        System.out.println("📦 Checking inventory for: " + (request != null ? request.getProductId() : "null"));

        if (request == null) {
            return InventoryResult.unavailable("Request is null");
        }

        if (request.getProductId() == null || request.getProductId().trim().isEmpty()) {
            return InventoryResult.unavailable("Product ID is required");
        }

        // Simple business logic - max 100 units available
        boolean available = request.getQuantity() <= 100;

        if (available) {
            System.out.println("✅ Inventory available for: " + request.getProductId() + " qty:" + request.getQuantity());
            return InventoryResult.available(request.getQuantity());
        } else {
            System.out.println("❌ Insufficient inventory for: " + request.getProductId() + " qty:" + request.getQuantity());
            return InventoryResult.unavailable("Insufficient inventory. Max 100 units available.");
        }
    }
}