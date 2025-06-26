package com.fc.serverless.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Enhanced property resolver supporting auth types while maintaining backward compatibility
 */
@Component
public class EnvironmentPropertyResolver {

    private static final Log log = LogFactory.getLog(EnvironmentPropertyResolver.class);

    private static final String ENV_PREFIX = "FC_LAMBDA_URL_";
    private static final String PROP_SUFFIX = ".url";
    private static final String FC_FUNCTION_PREFIX = "fc.functions.";
    private static final String FC_FUNCTION_URL_SUFFIX = ".url";
    private static final String FC_FUNCTION_PORT_SUFFIX = ".port";
    private static final String FC_FUNCTION_AUTH_TYPE_SUFFIX = ".authType";
    private static final String DEFAULT_LOCAL_HOST = "localhost";
    private static final int DEFAULT_BASE_PORT = 8080;

    public enum AuthType {
        NONE,
        AWS_IAM
    }

    public static class FunctionConfig {
        private final String url;
        private final AuthType authType;

        public FunctionConfig(String url, AuthType authType) {
            this.url = url;
            this.authType = authType;
        }

        public String getUrl() { return url; }
        public AuthType getAuthType() { return authType; }
    }

    /**
     * New method: Get complete function configuration including auth type
     */
    public FunctionConfig resolveFunctionConfig(String functionName, Environment environment) {
        String url = resolveUrl(functionName, environment);
        AuthType authType = resolveAuthType(functionName, environment);

        log.debug("Resolved config for " + functionName + ": url=" + url + ", authType=" + authType);
        return new FunctionConfig(url, authType);
    }

    /**
     * Existing method: Keep for backward compatibility
     */
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

        // 3. Try fc.functions.{functionName}.url (new format)
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
                log.error("Invalid port number for function " + functionName + ": " + portProperty);
            }
        }

        // 5. Generate default local development URL with auto-assigned port
        int port = generateDefaultPort(functionName, environment);
        return "http://" + DEFAULT_LOCAL_HOST + ":" + port + "/" + functionName;
    }

    private AuthType resolveAuthType(String functionName, Environment environment) {
        // Try fc.functions.{functionName}.authType
        String authTypeProperty = environment.getProperty(FC_FUNCTION_PREFIX + functionName + FC_FUNCTION_AUTH_TYPE_SUFFIX);

        if (authTypeProperty != null && !authTypeProperty.trim().isEmpty()) {
            try {
                return AuthType.valueOf(authTypeProperty.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("Invalid authType for " + functionName + ": " + authTypeProperty + ". Using NONE as default.");
            }
        }

        // Default to NONE for backward compatibility
        return AuthType.NONE;
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

    private int generateDefaultPort(String functionName, Environment environment) {
        // Check if there's a global base port configuration
        String basePortProperty = environment.getProperty("fc.functions.default.port.base");
        int basePort = DEFAULT_BASE_PORT;

        if (basePortProperty != null && !basePortProperty.trim().isEmpty()) {
            try {
                basePort = Integer.parseInt(basePortProperty.trim());
            } catch (NumberFormatException e) {
                log.warn("Invalid base port configuration: " + basePortProperty + ", using default: " + DEFAULT_BASE_PORT);
            }
        }

        // Generate port based on function name hash to ensure consistency
        int hash = Math.abs(functionName.toLowerCase().hashCode());
        int portOffset = hash % 100; // Limit offset to avoid high port numbers

        return basePort + portOffset;
    }
}