package com.fc.serverless.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cloud.function.adapter.aws.FunctionInvoker;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Generic Lambda handler for FC Serverless Framework
 *
 * This handler works with any Spring Cloud Function and automatically
 * handles the FC framework initialization.
 */
public class FcLambdaHandler implements RequestHandler<Map<String, Object>, Object> {

    private final FunctionInvoker invoker;
    private final ObjectMapper objectMapper;

    public FcLambdaHandler() {
        this.invoker = new FunctionInvoker();
        this.objectMapper = new ObjectMapper();

        // Set system properties for Lambda environment
        System.setProperty("spring.main.lazy-initialization", "true");
        System.setProperty("spring.cloud.function.web.export.enabled", "true");

        System.out.println("🚀 FC Lambda Handler initialized");
    }

    @Override
    public Object handleRequest(Map<String, Object> input, Context context) {
        try {
            System.out.println("🔄 FC Lambda processing request: " + context.getFunctionName());
            System.out.println("📥 Input: " + objectMapper.writeValueAsString(input));

            // Add request context for tracing
            System.setProperty("aws.lambda.request.id", context.getAwsRequestId());
            System.setProperty("aws.lambda.function.name", context.getFunctionName());

            // Convert input to InputStream for FunctionInvoker
            String inputJson = objectMapper.writeValueAsString(input);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(inputJson.getBytes(StandardCharsets.UTF_8));
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            // Process the request through Spring Cloud Function
            invoker.handleRequest(inputStream, outputStream, context);

            // Convert output back to Object
            String outputJson = outputStream.toString(StandardCharsets.UTF_8);
            Object result = objectMapper.readValue(outputJson, Object.class);

            System.out.println("📤 FC Lambda response: " + outputJson);
            System.out.println("✅ FC Lambda request completed successfully");

            return result;

        } catch (Exception e) {
            System.err.println("❌ FC Lambda error: " + e.getMessage());
            e.printStackTrace();

            // Return error response in consistent format
            return Map.of(
                    "error", true,
                    "message", "FC Lambda processing failed: " + e.getMessage(),
                    "requestId", context.getAwsRequestId()
            );
        }
    }
}