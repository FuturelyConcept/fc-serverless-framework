package com.fc.serverless.sample.domain;

/**
 * Result of user validation
 */
public class ValidationResult {
    private boolean valid;
    private String message;
    
    public ValidationResult() {}
    
    public ValidationResult(boolean valid) {
        this.valid = valid;
        this.message = valid ? "Valid" : "Invalid";
    }
    
    public ValidationResult(boolean valid, String message) {
        this.valid = valid;
        this.message = message;
    }
    
    public static ValidationResult valid() {
        return new ValidationResult(true, "Valid");
    }
    
    public static ValidationResult invalid(String message) {
        return new ValidationResult(false, message);
    }
    
    // Getters and setters
    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
