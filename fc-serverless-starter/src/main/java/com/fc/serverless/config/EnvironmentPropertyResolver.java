package com.fc.serverless.config;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Resolves remote function URLs from environment variables or application properties
 *
 * Priority:
 * 1. Environment variable: FC_LAMBDA_URL_{functionName} (uppercase)
 * 2. Application property: {functionName}.url
 * 3. Application property: fc.functions.{functionName}.url
 * 4. Application property: fc.functions.{functionName}.port (with localhost)
 * 5. Default local development URL with auto-assigned port
 */
@Component
public class EnvironmentPropertyResolver {

    private static final String ENV_PREFIX = "FC_LAMBDA_URL_";
    private static final String PROP_SUFFIX = ".url";
    private static final String FC_FUNCTION_PREFIX = "fc.functions.";
    private static final String FC_FUNCTION_URL_SUFFIX = ".url";
    private static final String FC_FUNCTION_PORT_SUFFIX = ".port";
    private static final String DEFAULT_LOCAL_HOST = "localhost";
    private static final int DEFAULT_BASE_PORT = 8080;

    public String resolveUrl(String functionName, Environment environment) {
        // 1. Try environment variable (AWS Lambda style)
        String envVarName = ENV_PREFIX + functionName.toUpperCase();
        String envUrl = System.getenv(envVarName);
        if (envUrl != null && !envUrl.trim().isEmpty()) {
            return normalizeUrl(envUrl, functionName);
        }

        // 2. Try direct application property (backward compatibility)
        String propUrl = environment.getProperty(functionName + PROP_SUFFIX);
        if (propUrl != null && !propUrl.trim().isEmpty()) {
            return normalizeUrl(propUrl, functionName);
        }

        // 3. Try fc.functions.{functionName}.url
        String fcFunctionUrl = environment.getProperty(FC_FUNCTION_PREFIX + functionName + FC_FUNCTION_URL_SUFFIX);
        if (fcFunctionUrl != null && !fcFunctionUrl.trim().isEmpty()) {
            return normalizeUrl(fcFunctionUrl, functionName);
        }

        // 4. Try fc.functions.{functionName}.port
        String portProperty = environment.getProperty(FC_FUNCTION_PREFIX + functionName + FC_FUNCTION_PORT_SUFFIX);
        if (portProperty != null && !portProperty.trim().isEmpty()) {
            try {
                int port = Integer.parseInt(portProperty.trim());
                return "http://" + DEFAULT_LOCAL_HOST + ":" + port + "/" + functionName;
            } catch (NumberFormatException e) {
                System.err.println("❌ FC Framework: Invalid port number for function " + functionName + ": " + portProperty);
            }
        }

        // 5. Generate default local development URL with auto-assigned port
        int port = generateDefaultPort(functionName, environment);
        return "http://" + DEFAULT_LOCAL_HOST + ":" + port + "/" + functionName;
    }

    private String normalizeUrl(String url, String functionName) {
        // Remove trailing slash
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }

        // Add function name if URL doesn't end with it
        if (!url.endsWith("/" + functionName)) {
            url = url + "/" + functionName;
        }

        return url;
    }

    /**
     * Generates a default port for local development based on function name hash
     * This ensures consistent port assignment across restarts while being generic
     */
    private int generateDefaultPort(String functionName, Environment environment) {
        // Check if there's a global base port configuration
        String basePortProperty = environment.getProperty("fc.functions.default.port.base");
        int basePort = DEFAULT_BASE_PORT;

        if (basePortProperty != null && !basePortProperty.trim().isEmpty()) {
            try {
                basePort = Integer.parseInt(basePortProperty.trim());
            } catch (NumberFormatException e) {
                System.err.println("❌ FC Framework: Invalid base port configuration: " + basePortProperty + ", using default: " + DEFAULT_BASE_PORT);
            }
        }

        // Generate port based on function name hash to ensure consistency
        // This gives us a deterministic port assignment while being generic
        int hash = Math.abs(functionName.toLowerCase().hashCode());
        int portOffset = hash % 100; // Limit offset to avoid high port numbers

        return basePort + portOffset;
    }
}