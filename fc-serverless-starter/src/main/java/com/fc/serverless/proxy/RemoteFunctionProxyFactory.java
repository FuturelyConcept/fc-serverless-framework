package com.fc.serverless.proxy;

import com.fc.serverless.core.annotation.RemoteFunction;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.function.*;

public class RemoteFunctionProxyFactory {

    private final RestTemplate restTemplate = new RestTemplate();

    public Object createProxy(Class<?> type, RemoteFunction annotation) {
        String functionName = annotation.name();

        InvocationHandler handler = (proxy, method, args) -> {
            // Very basic logic to convert args to JSON string and call REST
            String url = System.getenv(functionName.toUpperCase() + "_URL"); // Or use app.yml
            if (url == null) throw new RuntimeException("Missing URL for " + functionName);
            ResponseEntity<String> response = restTemplate.postForEntity(url, args[0], String.class);
            return response.getBody();
        };

        return Proxy.newProxyInstance(
                type.getClassLoader(),
                new Class<?>[]{type},
                handler
        );
    }
}
