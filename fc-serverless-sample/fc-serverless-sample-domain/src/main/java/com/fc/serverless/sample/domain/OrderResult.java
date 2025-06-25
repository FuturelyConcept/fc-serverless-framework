// Enhanced OrderResult.java
package com.fc.serverless.sample.domain;

public class OrderResult {
    private boolean success;
    private String orderId;
    private String transactionId;
    private String message;

    private OrderResult(boolean success, String orderId, String transactionId, String message) {
        this.success = success;
        this.orderId = orderId;
        this.transactionId = transactionId;
        this.message = message;
    }

    public static OrderResult success(String orderId) {
        return new OrderResult(true, orderId, null, "Order created successfully");
    }

    public static OrderResult success(String orderId, String transactionId) {
        return new OrderResult(true, orderId, transactionId, "Order created and payment processed successfully");
    }

    public static OrderResult failed(String message) {
        return new OrderResult(false, null, null, message);
    }

    // Getters
    public boolean isSuccess() { return success; }
    public String getOrderId() { return orderId; }
    public String getTransactionId() { return transactionId; }
    public String getMessage() { return message; }
}