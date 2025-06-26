package com.fc.serverless.sample.domain;

import java.math.BigDecimal;

public class PricingConfig {
    private BigDecimal basePrice;
    private BigDecimal premiumDiscount;
    private BigDecimal vipDiscount;
    private BigDecimal bulkDiscountThreshold;
    private BigDecimal bulkDiscountRate;

    public PricingConfig() {}

    public PricingConfig(BigDecimal basePrice, BigDecimal premiumDiscount, BigDecimal vipDiscount,
                         BigDecimal bulkDiscountThreshold, BigDecimal bulkDiscountRate) {
        this.basePrice = basePrice;
        this.premiumDiscount = premiumDiscount;
        this.vipDiscount = vipDiscount;
        this.bulkDiscountThreshold = bulkDiscountThreshold;
        this.bulkDiscountRate = bulkDiscountRate;
    }

    // Static factory method for easy creation
    public static PricingConfig defaultConfig() {
        return new PricingConfig(
                new BigDecimal("29.99"),    // Base price
                new BigDecimal("0.10"),     // 10% premium discount
                new BigDecimal("0.20"),     // 20% VIP discount
                new BigDecimal("10"),       // Bulk discount starts at 10 items
                new BigDecimal("0.05")      // 5% bulk discount
        );
    }

    // Getters and setters
    public BigDecimal getBasePrice() { return basePrice; }
    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }

    public BigDecimal getPremiumDiscount() { return premiumDiscount; }
    public void setPremiumDiscount(BigDecimal premiumDiscount) { this.premiumDiscount = premiumDiscount; }

    public BigDecimal getVipDiscount() { return vipDiscount; }
    public void setVipDiscount(BigDecimal vipDiscount) { this.vipDiscount = vipDiscount; }

    public BigDecimal getBulkDiscountThreshold() { return bulkDiscountThreshold; }
    public void setBulkDiscountThreshold(BigDecimal bulkDiscountThreshold) { this.bulkDiscountThreshold = bulkDiscountThreshold; }

    public BigDecimal getBulkDiscountRate() { return bulkDiscountRate; }
    public void setBulkDiscountRate(BigDecimal bulkDiscountRate) { this.bulkDiscountRate = bulkDiscountRate; }

    @Override
    public String toString() {
        return "PricingConfig{basePrice=" + basePrice + ", premiumDiscount=" + premiumDiscount +
                ", vipDiscount=" + vipDiscount + ", bulkThreshold=" + bulkDiscountThreshold +
                ", bulkRate=" + bulkDiscountRate + "}";
    }
}
