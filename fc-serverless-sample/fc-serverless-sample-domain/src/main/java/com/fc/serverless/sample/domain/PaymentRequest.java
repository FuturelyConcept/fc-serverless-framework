package com.fc.serverless.sample.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public class PaymentRequest {
    private String userId;
    private BigDecimal amount;
    private String currency;
    private String paymentMethod;
    private String orderId;

    public PaymentRequest() {}

    @JsonCreator
    public PaymentRequest(@JsonProperty("userId") String userId,
                          @JsonProperty("amount") BigDecimal amount,
                          @JsonProperty("currency") String currency,
                          @JsonProperty("paymentMethod") String paymentMethod,
                          @JsonProperty("orderId") String orderId) {
        this.userId = userId;
        this.amount = amount;
        this.currency = currency;
        this.paymentMethod = paymentMethod;
        this.orderId = orderId;
    }

    // Getters and setters with JSON annotations
    @JsonProperty("userId")
    public String getUserId() {
        return userId;
    }

    @JsonProperty("userId")
    public void setUserId(String userId) {
        this.userId = userId;
    }

    @JsonProperty("amount")
    public BigDecimal getAmount() {
        return amount;
    }

    @JsonProperty("amount")
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @JsonProperty("currency")
    public String getCurrency() {
        return currency;
    }

    @JsonProperty("currency")
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @JsonProperty("paymentMethod")
    public String getPaymentMethod() {
        return paymentMethod;
    }

    @JsonProperty("paymentMethod")
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    @JsonProperty("orderId")
    public String getOrderId() {
        return orderId;
    }

    @JsonProperty("orderId")
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    @Override
    public String toString() {
        return "PaymentRequest{" +
                "userId='" + userId + '\'' +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", orderId='" + orderId + '\'' +
                '}';
    }
}