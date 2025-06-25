package com.fc.serverless.sample.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class OrderResult {
    private boolean success;
    private String orderId;
    private String transactionId;
    private String message;

    private OrderResult() {}

    @JsonCreator
    private OrderResult(@JsonProperty("success") boolean success,
                        @JsonProperty("orderId") String orderId,
                        @JsonProperty("transactionId") String transactionId,
                        @JsonProperty("message") String message) {
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

    // Getters with JSON annotations
    @JsonProperty("success")
    public boolean isSuccess() { return success; }

    @JsonProperty("orderId")
    public String getOrderId() { return orderId; }

    @JsonProperty("transactionId")
    public String getTransactionId() { return transactionId; }

    @JsonProperty("message")
    public String getMessage() { return message; }

    @Override
    public String toString() {
        return "OrderResult{" +
                "success=" + success +
                ", orderId='" + orderId + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}