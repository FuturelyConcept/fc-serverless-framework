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
            // Very basic logic to convert args to JSON string and call REST
            String url = environment.getProperty(functionName + ".url");

            if (url == null) throw new RuntimeException("Missing URL for " + functionName);

            String jsonInput = objectMapper.writeValueAsString(args[0]);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<>(jsonInput, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            return objectMapper.readValue(response.getBody(), Object.class);
        };

        return Proxy.newProxyInstance(
                type.getClassLoader(),
                new Class<?>[]{type},
                handler
        );
    }
}
