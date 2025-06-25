package com.fc.serverless.proxy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fc.serverless.core.annotation.RemoteFunction;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class RemoteFunctionProxyFactory {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public RemoteFunctionProxyFactory() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public Object createProxy(Class<?> type, RemoteFunction annotation, Environment environment) {
        String functionName = annotation.name();

        InvocationHandler handler = (proxy, method, args) -> {
            try {
                // Get the URL for the remote function
                String url = environment.getProperty(functionName + ".url");

                if (url == null) {
                    throw new RuntimeException("Missing URL configuration for remote function: " + functionName +
                            ". Please add " + functionName + ".url property to application.yml");
                }

                System.out.println("🌐 Making remote call to: " + functionName + " at " + url);

                // Convert the first argument to JSON
                String jsonInput = objectMapper.writeValueAsString(args[0]);
                System.out.println("📤 Request: " + jsonInput);

                // Set up HTTP headers
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

                HttpEntity<String> request = new HttpEntity<>(jsonInput, headers);

                // Make the HTTP call
                ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
                System.out.println("📥 Response: " + response.getBody());

                // Convert response back to the expected return type
                Class<?> returnType = method.getReturnType();
                Object result = objectMapper.readValue(response.getBody(), returnType);

                System.out.println("✅ Remote call successful for: " + functionName);
                return result;

            } catch (Exception e) {
                System.err.println("❌ Remote call failed for: " + functionName + " - " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("Remote function call failed: " + functionName, e);
            }
        };

        return Proxy.newProxyInstance(
                type.getClassLoader(),
                new Class<?>[]{type},
                handler
        );
    }
}