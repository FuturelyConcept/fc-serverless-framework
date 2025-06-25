package com.fc.serverless.proxy;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fc.serverless.config.EnvironmentPropertyResolver;
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

    public Object createProxy(Class<?> functionType, RemoteFunction annotation, Environment environment, Class<?> returnType) {
        String functionName = annotation.name();

        InvocationHandler handler = (proxy, method, args) -> {
            try {
                String url = propertyResolver.resolveUrl(functionName, environment);

                System.out.println("🌐 FC Framework: Making remote call to: " + functionName + " at " + url);

                Object inputArg = args[0];
                String jsonInput = objectMapper.writeValueAsString(inputArg);
                System.out.println("📤 FC Request: " + jsonInput);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

                // Add custom headers for FC framework identification
                headers.set("X-FC-Framework", "true");
                headers.set("X-FC-Function", functionName);

                HttpEntity<String> request = new HttpEntity<>(jsonInput, headers);
                ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

                System.out.println("📥 FC Response status: " + response.getStatusCode());
                System.out.println("📥 FC Response body: " + response.getBody());

                Object result = objectMapper.readValue(response.getBody(), returnType);

                System.out.println("✅ FC Framework: Remote call successful for: " + functionName);
                return result;

            } catch (Exception e) {
                System.err.println("❌ FC Framework: Remote call failed for: " + functionName + " - " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("FC Framework: Remote function call failed: " + functionName, e);
            }
        };

        return Proxy.newProxyInstance(
                functionType.getClassLoader(),
                new Class<?>[]{functionType},
                handler
        );
    }

    // Backward compatibility method
    public Object createProxy(Class<?> functionType, RemoteFunction annotation, Environment environment) {
        return createProxy(functionType, annotation, environment, Object.class);
    }
}