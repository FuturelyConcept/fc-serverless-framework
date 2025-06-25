package com.fc.serverless.paymentprocessor;

import com.fc.serverless.sample.domain.PaymentRequest;
import com.fc.serverless.sample.domain.PaymentResult;

import java.math.BigDecimal;
import java.util.function.Function;

public class PaymentProcessorFunction implements Function<PaymentRequest, PaymentResult> {

    private static final BigDecimal MAX_AMOUNT = new BigDecimal("10000.00");

    @Override
    public PaymentResult apply(PaymentRequest request) {
        System.out.println("🚀 DEDICATED PaymentProcessor Lambda executing!");
        System.out.println("💳 Processing payment for: " + (request != null ? request.getUserId() : "null"));

        if (request == null) {
            System.out.println("❌ Payment request is null");
            return PaymentResult.failed("Payment request is null");
        }

        // Validate required fields
        if (request.getUserId() == null || request.getUserId().trim().isEmpty()) {
            System.out.println("❌ User ID is required");
            return PaymentResult.failed("User ID is required");
        }

        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("❌ Invalid payment amount: " + request.getAmount());
            return PaymentResult.failed("Invalid payment amount");
        }

        if (request.getAmount().compareTo(MAX_AMOUNT) > 0) {
            System.out.println("❌ Payment amount exceeds maximum: " + request.getAmount());
            return PaymentResult.failed("Payment amount exceeds maximum limit of " + MAX_AMOUNT);
        }

        if (request.getPaymentMethod() == null || request.getPaymentMethod().trim().isEmpty()) {
            System.out.println("❌ Payment method is required");
            return PaymentResult.failed("Payment method is required");
        }

        // Simulate payment processing logic
        if ("CARD".equals(request.getPaymentMethod())) {
            String transactionId = "txn-" + System.currentTimeMillis();
            System.out.println("✅ Card payment successful: " + transactionId + " for amount: " + request.getAmount());
            return PaymentResult.success(transactionId, request.getAmount());
        } else if ("BANK_TRANSFER".equals(request.getPaymentMethod())) {
            String transactionId = "txn-" + System.currentTimeMillis();
            System.out.println("⏳ Bank transfer pending: " + transactionId + " for amount: " + request.getAmount());
            return PaymentResult.pending(transactionId, request.getAmount());
        } else {
            System.out.println("❌ Unsupported payment method: " + request.getPaymentMethod());
            return PaymentResult.failed("Unsupported payment method: " + request.getPaymentMethod());
        }
    }
}