package com.fc.serverless.proxy;

import com.fc.serverless.core.annotation.RemoteFunction;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.function.*;

public class RemoteFunctionProxyFactory {

    private final RestTemplate restTemplate = new RestTemplate();

    public Object createProxy(Class<?> type, RemoteFunction annotation, Environment environment) {
        String functionName = annotation.name();

        InvocationHandler handler = (proxy, method, args) -> {
            // Very basic logic to convert args to JSON string and call REST
            String url = environment.getProperty(functionName + ".url");
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
