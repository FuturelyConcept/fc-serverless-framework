package com.fc.serverless.sample.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class InventoryResult {
    private boolean available;
    private int availableQuantity;
    private String message;

    private InventoryResult() {}

    @JsonCreator
    private InventoryResult(@JsonProperty("available") boolean available,
                            @JsonProperty("availableQuantity") int availableQuantity,
                            @JsonProperty("message") String message) {
        this.available = available;
        this.availableQuantity = availableQuantity;
        this.message = message;
    }

    public static InventoryResult available(int quantity) {
        return new InventoryResult(true, quantity, "Inventory available");
    }

    public static InventoryResult unavailable(String message) {
        return new InventoryResult(false, 0, message);
    }

    @JsonProperty("available")
    public boolean isAvailable() { return available; }

    @JsonProperty("availableQuantity")
    public int getAvailableQuantity() { return availableQuantity; }

    @JsonProperty("message")
    public String getMessage() { return message; }

    @Override
    public String toString() {
        return "InventoryResult{" +
                "available=" + available +
                ", availableQuantity=" + availableQuantity +
                ", message='" + message + '\'' +
                '}';
    }
}