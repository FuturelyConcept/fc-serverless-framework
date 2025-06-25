// PaymentRequest.java
package com.fc.serverless.sample.domain;

import java.math.BigDecimal;

public class PaymentRequest {
    private String userId;
    private BigDecimal amount;
    private String currency;
    private String paymentMethod;
    private String orderId;

    public PaymentRequest() {}

    public PaymentRequest(String userId, BigDecimal amount, String currency, String paymentMethod, String orderId) {
        this.userId = userId;
        this.amount = amount;
        this.currency = currency;
        this.paymentMethod = paymentMethod;
        this.orderId = orderId;
    }

    // Getters and setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
}