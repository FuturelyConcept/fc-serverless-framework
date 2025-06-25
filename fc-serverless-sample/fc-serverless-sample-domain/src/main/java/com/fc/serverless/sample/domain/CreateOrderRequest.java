// Enhanced CreateOrderRequest.java
package com.fc.serverless.sample.domain;

public class CreateOrderRequest {
    private UserData userData;
    private String productId;
    private int quantity;
    private String paymentMethod;

    public CreateOrderRequest() {}

    public CreateOrderRequest(UserData userData, String productId, int quantity) {
        this.userData = userData;
        this.productId = productId;
        this.quantity = quantity;
        this.paymentMethod = "CARD"; // Default payment method
    }

    public CreateOrderRequest(UserData userData, String productId, int quantity, String paymentMethod) {
        this.userData = userData;
        this.productId = productId;
        this.quantity = quantity;
        this.paymentMethod = paymentMethod;
    }

    // Getters and setters
    public UserData getUserData() { return userData; }
    public void setUserData(UserData userData) { this.userData = userData; }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
}
