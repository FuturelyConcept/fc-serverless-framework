package com.fc.serverless.sample.domain;

public class OrderRequest {
    private String productId;
    private int quantity;
    private String customerType; // REGULAR, PREMIUM, VIP

    public OrderRequest() {}

    public OrderRequest(String productId, int quantity, String customerType) {
        this.productId = productId;
        this.quantity = quantity;
        this.customerType = customerType;
    }

    // Getters and setters
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getCustomerType() { return customerType; }
    public void setCustomerType(String customerType) { this.customerType = customerType; }

    @Override
    public String toString() {
        return "OrderRequest{productId='" + productId + "', quantity=" + quantity +
                ", customerType='" + customerType + "'}";
    }
}