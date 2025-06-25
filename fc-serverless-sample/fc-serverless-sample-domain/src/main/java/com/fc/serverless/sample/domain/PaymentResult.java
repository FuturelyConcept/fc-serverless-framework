package com.fc.serverless.sample.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public class PaymentResult {
    private boolean success;
    private String transactionId;
    private BigDecimal processedAmount;
    private String message;
    private String status;

    private PaymentResult() {}

    @JsonCreator
    private PaymentResult(@JsonProperty("success") boolean success,
                          @JsonProperty("transactionId") String transactionId,
                          @JsonProperty("processedAmount") BigDecimal processedAmount,
                          @JsonProperty("message") String message,
                          @JsonProperty("status") String status) {
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

    // Getters with JSON annotations
    @JsonProperty("success")
    public boolean isSuccess() { return success; }

    @JsonProperty("transactionId")
    public String getTransactionId() { return transactionId; }

    @JsonProperty("processedAmount")
    public BigDecimal getProcessedAmount() { return processedAmount; }

    @JsonProperty("message")
    public String getMessage() { return message; }

    @JsonProperty("status")
    public String getStatus() { return status; }

    @Override
    public String toString() {
        return "PaymentResult{" +
                "success=" + success +
                ", transactionId='" + transactionId + '\'' +
                ", processedAmount=" + processedAmount +
                ", message='" + message + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}