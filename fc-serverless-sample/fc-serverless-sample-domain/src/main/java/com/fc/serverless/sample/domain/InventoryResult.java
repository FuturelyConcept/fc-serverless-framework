package com.fc.serverless.sample.domain;

public class InventoryResult {
    private boolean available;
    private int availableQuantity;
    private String message;

    private InventoryResult(boolean available, int availableQuantity, String message) {
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

    public boolean isAvailable() { return available; }
    public int getAvailableQuantity() { return availableQuantity; }
    public String getMessage() { return message; }
}