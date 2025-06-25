package com.fc.serverless.pricecalculator;

import com.fc.serverless.sample.domain.OrderRequest;
import com.fc.serverless.sample.domain.PriceInfo;
import com.fc.serverless.sample.domain.PricingConfig;
import com.fc.serverless.core.annotation.RemoteFunction;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Lambda 2: PriceCalculator
 *
 * Calculates pricing for orders by getting configuration from ConfigSupplier
 * and applying business rules.
 *
 * Demonstrates: Supplier<PricingConfig> remote call (no input parameters)
 */
public class PriceCalculatorFunction implements Function<OrderRequest, PriceInfo> {

    // FC Framework automatically creates an HTTP proxy for this Supplier!
    @RemoteFunction(name = "configSupplier")
    private Supplier<PricingConfig> configSupplier;

    @Override
    public PriceInfo apply(OrderRequest request) {
        System.out.println("💰 PriceCalculator Lambda started!");
        System.out.println("🔢 Calculating price for: " + request);

        try {
            // Get pricing configuration from ConfigSupplier Lambda
            // 🌐 This becomes an HTTP GET call to ConfigSupplier Lambda!
            System.out.println("⚙️ Getting pricing config from ConfigSupplier...");
            PricingConfig config = configSupplier.get();

            if (config == null) {
                throw new RuntimeException("Failed to get pricing configuration");
            }

            System.out.println("✅ Config retrieved: " + config);

            // Calculate pricing based on configuration and business rules
            BigDecimal unitPrice = config.getBasePrice();
            BigDecimal quantity = new BigDecimal(request.getQuantity());
            BigDecimal subtotal = unitPrice.multiply(quantity);

            // Apply customer type discount
            BigDecimal discount = BigDecimal.ZERO;
            String discountReason = "No discount applied.";

            switch (request.getCustomerType().toUpperCase()) {
                case "PREMIUM":
                    discount = subtotal.multiply(config.getPremiumDiscount());
                    discountReason = "Premium customer discount applied.";
                    break;
                case "VIP":
                    discount = subtotal.multiply(config.getVipDiscount());
                    discountReason = "VIP customer discount applied.";
                    break;
                default:
                    discountReason = "Regular customer pricing.";
            }

            // Apply bulk discount if applicable
            if (quantity.compareTo(config.getBulkDiscountThreshold()) >= 0) {
                BigDecimal bulkDiscount = subtotal.multiply(config.getBulkDiscountRate());
                if (bulkDiscount.compareTo(discount) > 0) {
                    discount = bulkDiscount;
                    discountReason = "Bulk order discount applied.";
                }
            }

            // Calculate final price
            BigDecimal totalPrice = subtotal.subtract(discount);
            totalPrice = totalPrice.setScale(2, RoundingMode.HALF_UP);
            discount = discount.setScale(2, RoundingMode.HALF_UP);

            PriceInfo result = new PriceInfo(unitPrice, totalPrice, discount, discountReason);

            System.out.println("✅ Price calculation completed: " + result);
            return result;

        } catch (Exception e) {
            System.err.println("❌ Error calculating price: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Price calculation failed: " + e.getMessage());
        }
    }
}