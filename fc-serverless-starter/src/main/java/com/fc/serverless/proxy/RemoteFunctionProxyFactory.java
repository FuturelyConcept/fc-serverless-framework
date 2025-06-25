package com.fc.serverless.proxy;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fc.serverless.config.EnvironmentPropertyResolver;
import com.fc.serverless.config.RemoteFunctionBeanPostProcessor.FunctionalInterfaceInfo;
import com.fc.serverless.core.annotation.RemoteFunction;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class RemoteFunctionProxyFactory {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final EnvironmentPropertyResolver propertyResolver;

    public RemoteFunctionProxyFactory() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = createObjectMapper();
        this.propertyResolver = new EnvironmentPropertyResolver();
    }

    private ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return mapper;
    }

    /**
     * Enhanced proxy creation method that supports Function, Supplier, and Consumer
     */
    public Object createProxy(Class<?> functionType, RemoteFunction annotation, Environment environment, FunctionalInterfaceInfo interfaceInfo) {
        String functionName = annotation.name();

        InvocationHandler handler = (proxy, method, args) -> {
            try {
                String url = propertyResolver.resolveUrl(functionName, environment);

                System.out.println("🌐 FC Framework: Making remote " + interfaceInfo.interfaceType +
                        " call to: " + functionName + " at " + url);

                // Handle different functional interface types
                switch (interfaceInfo.interfaceType) {
                    case "FUNCTION":
                        return handleFunctionCall(url, functionName, args, interfaceInfo);
                    case "SUPPLIER":
                        return handleSupplierCall(url, functionName, interfaceInfo);
                    case "CONSUMER":
                        return handleConsumerCall(url, functionName, args, interfaceInfo);
                    default:
                        throw new UnsupportedOperationException("Unsupported functional interface: " + interfaceInfo.interfaceType);
                }

            } catch (Exception e) {
                System.err.println("❌ FC Framework: Remote " + interfaceInfo.interfaceType +
                        " call failed for: " + functionName + " - " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("FC Framework: Remote " + interfaceInfo.interfaceType +
                        " call failed: " + functionName, e);
            }
        };

        return Proxy.newProxyInstance(
                functionType.getClassLoader(),
                new Class<?>[]{functionType},
                handler
        );
    }

    /**
     * Handle Function<T,R> calls - standard request/response pattern
     */
    private Object handleFunctionCall(String url, String functionName, Object[] args, FunctionalInterfaceInfo interfaceInfo) throws Exception {
        Object inputArg = args[0];
        String jsonInput = objectMapper.writeValueAsString(inputArg);
        System.out.println("📤 FC Function Request: " + jsonInput);

        HttpHeaders headers = createHeaders(functionName, "FUNCTION");
        HttpEntity<String> request = new HttpEntity<>(jsonInput, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        System.out.println("📥 FC Function Response status: " + response.getStatusCode());
        System.out.println("📥 FC Function Response body: " + response.getBody());

        Object result = objectMapper.readValue(response.getBody(), interfaceInfo.outputType);
        System.out.println("✅ FC Framework: Remote Function call successful for: " + functionName);
        return result;
    }

    /**
     * Handle Supplier<T> calls - no input, just response
     */
    private Object handleSupplierCall(String url, String functionName, FunctionalInterfaceInfo interfaceInfo) throws Exception {
        System.out.println("📤 FC Supplier Request: (no input)");

        HttpHeaders headers = createHeaders(functionName, "SUPPLIER");
        HttpEntity<String> request = new HttpEntity<>(headers);

        // Use GET for Supplier calls since there's no input
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

        System.out.println("📥 FC Supplier Response status: " + response.getStatusCode());
        System.out.println("📥 FC Supplier Response body: " + response.getBody());

        Object result = objectMapper.readValue(response.getBody(), interfaceInfo.outputType);
        System.out.println("✅ FC Framework: Remote Supplier call successful for: " + functionName);
        return result;
    }

    /**
     * Handle Consumer<T> calls - input but no response (void)
     */
    private Object handleConsumerCall(String url, String functionName, Object[] args, FunctionalInterfaceInfo interfaceInfo) throws Exception {
        Object inputArg = args[0];
        String jsonInput = objectMapper.writeValueAsString(inputArg);
        System.out.println("📤 FC Consumer Request: " + jsonInput);

        HttpHeaders headers = createHeaders(functionName, "CONSUMER");
        HttpEntity<String> request = new HttpEntity<>(jsonInput, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        System.out.println("📥 FC Consumer Response status: " + response.getStatusCode());
        // Consumer doesn't need to process response body, just verify success

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Consumer call failed with status: " + response.getStatusCode());
        }

        System.out.println("✅ FC Framework: Remote Consumer call successful for: " + functionName);
        return null; // Consumer returns void
    }

    /**
     * Create common headers for all function types
     */
    private HttpHeaders createHeaders(String functionName, String interfaceType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.set("X-FC-Framework", "true");
        headers.set("X-FC-Function", functionName);
        headers.set("X-FC-Interface-Type", interfaceType);
        return headers;
    }

    // Backward compatibility methods
    public Object createProxy(Class<?> functionType, RemoteFunction annotation, Environment environment, Class<?> returnType) {
        // Create a basic FunctionalInterfaceInfo for backward compatibility
        FunctionalInterfaceInfo interfaceInfo = new FunctionalInterfaceInfo("FUNCTION", Object.class, returnType, returnType);
        return createProxy(functionType, annotation, environment, interfaceInfo);
    }

    public Object createProxy(Class<?> functionType, RemoteFunction annotation, Environment environment) {
        return createProxy(functionType, annotation, environment, Object.class);
    }
}