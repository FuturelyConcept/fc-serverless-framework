package com.fc.serverless.sample.domain;

import java.math.BigDecimal;

public class OrderResult {
    private boolean success;
    private String orderId;
    private BigDecimal totalPrice;
    private String message;

    public OrderResult() {}

    private OrderResult(boolean success, String orderId, BigDecimal totalPrice, String message) {
        this.success = success;
        this.orderId = orderId;
        this.totalPrice = totalPrice;
        this.message = message;
    }

    public static OrderResult success(String orderId, BigDecimal totalPrice, String message) {
        return new OrderResult(true, orderId, totalPrice, message);
    }

    public static OrderResult failed(String message) {
        return new OrderResult(false, null, null, message);
    }

    // Getters and setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    @Override
    public String toString() {
        return "OrderResult{success=" + success + ", orderId='" + orderId +
                "', totalPrice=" + totalPrice + ", message='" + message + "'}";
    }
}