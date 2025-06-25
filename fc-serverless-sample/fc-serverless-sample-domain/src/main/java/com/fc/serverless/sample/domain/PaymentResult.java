// PaymentResult.java
package com.fc.serverless.sample.domain;

import java.math.BigDecimal;

public class PaymentResult {
    private boolean success;
    private String transactionId;
    private BigDecimal processedAmount;
    private String message;
    private String status;

    private PaymentResult(boolean success, String transactionId, BigDecimal processedAmount, String message, String status) {
        this.success = success;
        this.transactionId = transactionId;
        this.processedAmount = processedAmount;
        this.message = message;
        this.status = status;
    }

    public static PaymentResult success(String transactionId, BigDecimal amount) {
        return new PaymentResult(true, transactionId, amount, "Payment processed successfully", "COMPLETED");
    }

    public static PaymentResult failed(String message) {
        return new PaymentResult(false, null, null, message, "FAILED");
    }

    public static PaymentResult pending(String transactionId, BigDecimal amount) {
        return new PaymentResult(false, transactionId, amount, "Payment is being processed", "PENDING");
    }

    // Getters
    public boolean isSuccess() { return success; }
    public String getTransactionId() { return transactionId; }
    public BigDecimal getProcessedAmount() { return processedAmount; }
    public String getMessage() { return message; }
    public String getStatus() { return status; }
}