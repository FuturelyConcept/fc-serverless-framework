package com.fc.serverless.sample.domain;

import java.math.BigDecimal;

public class PriceInfo {
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private BigDecimal discount;
    private String discountReason;

    public PriceInfo() {}

    public PriceInfo(BigDecimal unitPrice, BigDecimal totalPrice, BigDecimal discount, String discountReason) {
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
        this.discount = discount;
        this.discountReason = discountReason;
    }

    // Getters and setters
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }

    public BigDecimal getDiscount() { return discount; }
    public void setDiscount(BigDecimal discount) { this.discount = discount; }

    public String getDiscountReason() { return discountReason; }
    public void setDiscountReason(String discountReason) { this.discountReason = discountReason; }

    @Override
    public String toString() {
        return "PriceInfo{unitPrice=" + unitPrice + ", totalPrice=" + totalPrice +
                ", discount=" + discount + ", discountReason='" + discountReason + "'}";
    }
}