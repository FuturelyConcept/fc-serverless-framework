package com.fc.serverless.pricecalculator;

import com.fc.serverless.sample.domain.OrderRequest;
import com.fc.serverless.sample.domain.PriceInfo;
import com.fc.serverless.sample.domain.PricingConfig;
import com.fc.serverless.core.annotation.RemoteFunction;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Updated PriceCalculator Lambda - Now with LogFactory instead of System.out
 * IAM-protected service that calls ConfigSupplier (NONE/public)
 *
 * Minimal changes: Only replaced System.out with LogFactory logging
 */
public class PriceCalculatorFunction implements Function<OrderRequest, PriceInfo> {

    private static final Log log = LogFactory.getLog(PriceCalculatorFunction.class);

    // FC Framework automatically creates an HTTP proxy for this Supplier!
    @RemoteFunction(name = "configSupplier")
    private Supplier<PricingConfig> configSupplier;

    @Override
    public PriceInfo apply(OrderRequest request) {
        log.info("💰 PriceCalculator Lambda started!");
        log.info("🔢 Calculating price for: " + request);

        try {
            // Get pricing configuration from ConfigSupplier Lambda
            // 🌐 This becomes an HTTP GET call to ConfigSupplier Lambda!
            log.info("⚙️ Getting pricing config from ConfigSupplier...");
            PricingConfig config = configSupplier.get();

            if (config == null) {
                throw new RuntimeException("Failed to get pricing configuration");
            }

            log.info("✅ Config retrieved: " + config);

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

            BigDecimal finalPrice = subtotal.subtract(discount);

            PriceInfo result = new PriceInfo(unitPrice, finalPrice, discount, discountReason);

            log.info("✅ Price calculation completed: " + result);
            log.info("🔐 PriceCalculator successfully processed IAM-protected request");

            return result;

        } catch (Exception e) {
            log.error("❌ Error calculating price: " + e.getMessage(), e);
            throw new RuntimeException("Price calculation failed: " + e.getMessage(), e);
        }
    }
}