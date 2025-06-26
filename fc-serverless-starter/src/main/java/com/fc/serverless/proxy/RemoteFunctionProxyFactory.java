package com.fc.serverless.proxy;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fc.serverless.auth.AwsIamRequestSigner;
import com.fc.serverless.config.EnvironmentPropertyResolver;
import com.fc.serverless.config.EnvironmentPropertyResolver.AuthType;
import com.fc.serverless.config.EnvironmentPropertyResolver.FunctionConfig;
import com.fc.serverless.core.annotation.RemoteFunction;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

    private static final Log log = LogFactory.getLog(RemoteFunctionProxyFactory.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final EnvironmentPropertyResolver propertyResolver;
    private final AwsIamRequestSigner iamSigner;

    public RemoteFunctionProxyFactory() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = createObjectMapper();
        this.propertyResolver = new EnvironmentPropertyResolver();
        this.iamSigner = new AwsIamRequestSigner();
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

        // Use new enhanced config resolution if available, fallback to old method
        final FunctionConfig config = propertyResolver.resolveFunctionConfig(functionName, environment);


        log.info("🔗 FC Framework: Creating proxy for " + functionName + " at " + config.getUrl() +
                " with auth: " + config.getAuthType());

        InvocationHandler handler = (proxy, method, args) -> {
            try {
                return invokeRemoteFunction(functionName, config, functionType, args, returnType);
            } catch (Exception e) {
                log.error("❌ FC Framework: Remote call failed for: " + functionName, e);
                throw new RuntimeException("FC Framework: Remote function call failed: " + functionName, e);
            }
        };

        return Proxy.newProxyInstance(
                functionType.getClassLoader(),
                new Class<?>[]{functionType},
                handler
        );
    }

    private Object invokeRemoteFunction(String functionName, FunctionConfig config, Class<?> functionType,
                                        Object[] args, Class<?> returnType) throws Exception {

        String url = config.getUrl();
        AuthType authType = config.getAuthType();

        log.info("🌐 FC Framework: Making remote call to: " + functionName + " at " + url);

        // Handle different function interface types
        Object inputArg = extractInputArgument(functionType, args);
        String jsonInput = null;

        if (inputArg != null) {
            jsonInput = objectMapper.writeValueAsString(inputArg);
            log.debug("📤 FC Request: " + jsonInput);
        }

        // Prepare headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.set("X-FC-Framework", "true");
        headers.set("X-FC-Function", functionName);

        // Apply authentication if required
        if (authType == AuthType.AWS_IAM) {
            log.debug("🔐 Applying AWS IAM authentication");
            headers = iamSigner.signRequest(url, HttpMethod.POST, headers, jsonInput);
        }

        // Make the HTTP request
        HttpEntity<String> request = new HttpEntity<>(jsonInput, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        log.debug("📥 FC Response status: " + response.getStatusCode());
        log.debug("📥 FC Response body: " + response.getBody());

        // Handle different return types based on functional interface
        return processResponse(functionType, response.getBody(), returnType);
    }

    /**
     * Extract input argument based on functional interface type
     */
    private Object extractInputArgument(Class<?> functionType, Object[] args) {
        if (isConsumerType(functionType)) {
            // Consumer<T> - has input argument, no return value
            return (args != null && args.length > 0) ? args[0] : null;
        } else if (isSupplierType(functionType)) {
            // Supplier<T> - no input argument, has return value
            return null;
        } else if (isFunctionType(functionType)) {
            // Function<T,R> - has input argument and return value
            return (args != null && args.length > 0) ? args[0] : null;
        } else {
            // Default: assume first argument is input
            return (args != null && args.length > 0) ? args[0] : null;
        }
    }

    /**
     * Process response based on functional interface type
     */
    private Object processResponse(Class<?> functionType, String responseBody, Class<?> returnType) throws Exception {
        if (isConsumerType(functionType)) {
            // Consumer<T> - no return value
            return null;
        } else if (isSupplierType(functionType) || isFunctionType(functionType)) {
            // Supplier<T> or Function<T,R> - deserialize response
            if (responseBody == null || responseBody.trim().isEmpty()) {
                return null;
            }
            return objectMapper.readValue(responseBody, returnType);
        } else {
            // Default: deserialize response
            if (responseBody == null || responseBody.trim().isEmpty()) {
                return null;
            }
            return objectMapper.readValue(responseBody, returnType);
        }
    }

    /**
     * Type checking helper methods
     */
    private boolean isConsumerType(Class<?> type) {
        return Consumer.class.isAssignableFrom(type);
    }

    private boolean isSupplierType(Class<?> type) {
        return Supplier.class.isAssignableFrom(type);
    }

    private boolean isFunctionType(Class<?> type) {
        return Function.class.isAssignableFrom(type);
    }

    // Backward compatibility method
    public Object createProxy(Class<?> functionType, RemoteFunction annotation, Environment environment) {
        return createProxy(functionType, annotation, environment, Object.class);
    }
}