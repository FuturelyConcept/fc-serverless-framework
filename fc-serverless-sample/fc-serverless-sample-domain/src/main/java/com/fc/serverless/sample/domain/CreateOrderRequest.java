package com.fc.serverless.sample.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateOrderRequest {
    private UserData userData;
    private String productId;
    private int quantity;
    private String paymentMethod;

    public CreateOrderRequest() {}

    @JsonCreator
    public CreateOrderRequest(@JsonProperty("userData") UserData userData,
                              @JsonProperty("productId") String productId,
                              @JsonProperty("quantity") int quantity,
                              @JsonProperty("paymentMethod") String paymentMethod) {
        this.userData = userData;
        this.productId = productId;
        this.quantity = quantity;
        this.paymentMethod = paymentMethod != null ? paymentMethod : "CARD"; // Default payment method
    }

    public CreateOrderRequest(UserData userData, String productId, int quantity) {
        this(userData, productId, quantity, "CARD");
    }

    // Getters and setters with JSON annotations
    @JsonProperty("userData")
    public UserData getUserData() { return userData; }

    @JsonProperty("userData")
    public void setUserData(UserData userData) { this.userData = userData; }

    @JsonProperty("productId")
    public String getProductId() { return productId; }

    @JsonProperty("productId")
    public void setProductId(String productId) { this.productId = productId; }

    @JsonProperty("quantity")
    public int getQuantity() { return quantity; }

    @JsonProperty("quantity")
    public void setQuantity(int quantity) { this.quantity = quantity; }

    @JsonProperty("paymentMethod")
    public String getPaymentMethod() { return paymentMethod; }

    @JsonProperty("paymentMethod")
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    @Override
    public String toString() {
        return "CreateOrderRequest{" +
                "userData=" + userData +
                ", productId='" + productId + '\'' +
                ", quantity=" + quantity +
                ", paymentMethod='" + paymentMethod + '\'' +
                '}';
    }
}