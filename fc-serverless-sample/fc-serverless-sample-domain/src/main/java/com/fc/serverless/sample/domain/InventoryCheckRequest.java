package com.fc.serverless.sample.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class InventoryCheckRequest {
    private String productId;
    private int quantity;

    public InventoryCheckRequest() {}

    @JsonCreator
    public InventoryCheckRequest(@JsonProperty("productId") String productId,
                                 @JsonProperty("quantity") int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    @JsonProperty("productId")
    public String getProductId() { return productId; }

    @JsonProperty("productId")
    public void setProductId(String productId) { this.productId = productId; }

    @JsonProperty("quantity")
    public int getQuantity() { return quantity; }

    @JsonProperty("quantity")
    public void setQuantity(int quantity) { this.quantity = quantity; }

    @Override
    public String toString() {
        return "InventoryCheckRequest{" +
                "productId='" + productId + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}