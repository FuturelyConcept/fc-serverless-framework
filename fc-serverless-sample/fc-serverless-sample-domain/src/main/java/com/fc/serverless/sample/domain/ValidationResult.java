package com.fc.serverless.sample.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Result of user validation
 */
public class ValidationResult {
    private boolean valid;
    private String message;

    public ValidationResult() {}

    @JsonCreator
    public ValidationResult(@JsonProperty("valid") boolean valid,
                            @JsonProperty("message") String message) {
        this.valid = valid;
        this.message = message;
    }

    public ValidationResult(boolean valid) {
        this.valid = valid;
        this.message = valid ? "Valid" : "Invalid";
    }

    public static ValidationResult valid() {
        return new ValidationResult(true, "Valid");
    }

    public static ValidationResult invalid(String message) {
        return new ValidationResult(false, message);
    }

    // Getters and setters with JSON annotations
    @JsonProperty("valid")
    public boolean isValid() { return valid; }

    @JsonProperty("valid")
    public void setValid(boolean valid) { this.valid = valid; }

    @JsonProperty("message")
    public String getMessage() { return message; }

    @JsonProperty("message")
    public void setMessage(String message) { this.message = message; }

    @Override
    public String toString() {
        return "ValidationResult{" +
                "valid=" + valid +
                ", message='" + message + '\'' +
                '}';
    }
}