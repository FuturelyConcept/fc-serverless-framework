package com.fc.serverless.proxy;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
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
        this.objectMapper = createObjectMapper();
    }

    private ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        return mapper;
    }

    public Object createProxy(Class<?> functionType, RemoteFunction annotation, Environment environment, Class<?> returnType) {
        String functionName = annotation.name();

        InvocationHandler handler = (proxy, method, args) -> {
            try {
                String url = environment.getProperty(functionName + ".url");

                if (url == null) {
                    throw new RuntimeException("Missing URL configuration for remote function: " + functionName +
                            ". Please add " + functionName + ".url property to application.yml");
                }

                System.out.println("🌐 Making remote call to: " + functionName + " at " + url);

                Object inputArg = args[0];
                String jsonInput = objectMapper.writeValueAsString(inputArg);
                System.out.println("📤 Request: " + jsonInput);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

                HttpEntity<String> request = new HttpEntity<>(jsonInput, headers);
                ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

                System.out.println("📥 Response status: " + response.getStatusCode());
                System.out.println("📥 Response body: " + response.getBody());
                System.out.println("🔍 Expected return type: " + returnType.getName());

                Object result = objectMapper.readValue(response.getBody(), returnType);

                System.out.println("✅ Remote call successful for: " + functionName);
                System.out.println("🔍 Actual result type: " + result.getClass().getName());
                return result;

            } catch (Exception e) {
                System.err.println("❌ Remote call failed for: " + functionName + " - " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("Remote function call failed: " + functionName, e);
            }
        };

        return Proxy.newProxyInstance(
                functionType.getClassLoader(),
                new Class<?>[]{functionType},
                handler
        );
    }

    // Backward compatibility method - delegates to the new method with Object.class as return type
    public Object createProxy(Class<?> functionType, RemoteFunction annotation, Environment environment) {
        return createProxy(functionType, annotation, environment, Object.class);
    }
}