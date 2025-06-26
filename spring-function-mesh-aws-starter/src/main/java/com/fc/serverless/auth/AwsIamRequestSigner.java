package com.fc.serverless.auth;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.signer.Aws4Signer;
import software.amazon.awssdk.auth.signer.params.Aws4SignerParams;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.http.SdkHttpMethod;
import software.amazon.awssdk.regions.Region;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.util.List;
import java.util.Map;

/**
 * AWS IAM request signer for signing HTTP requests to Lambda functions
 * using AWS SDK v2 SigV4 signing process.
 */
public class AwsIamRequestSigner {

    private static final Log log = LogFactory.getLog(AwsIamRequestSigner.class);

    private static final String SERVICE_NAME = "lambda";
    private final DefaultCredentialsProvider credentialsProvider;
    private final Aws4Signer signer;

    public AwsIamRequestSigner() {
        this.credentialsProvider = DefaultCredentialsProvider.create();
        this.signer = Aws4Signer.create();
        log.info("AWS IAM Request Signer initialized");
    }

    /**
     * Signs an HTTP request using AWS SigV4 algorithm
     *
     * @param url The request URL
     * @param method The HTTP method
     * @param headers The existing request headers
     * @param body The request body (can be null)
     * @return Updated headers with AWS signature
     */
    public HttpHeaders signRequest(String url, HttpMethod method, HttpHeaders headers, String body) {
        try {
            log.debug("Signing AWS IAM request for URL: " + url);

            URI uri = new URI(url);
            Region region = extractRegionFromUrl(url);

            // Build the SDK HTTP request
            SdkHttpFullRequest.Builder requestBuilder = SdkHttpFullRequest.builder()
                    .uri(uri)
                    .method(convertToSdkHttpMethod(method));

            // Add existing headers
            if (headers != null) {
                for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
                    String headerName = entry.getKey();
                    List<String> headerValues = entry.getValue();

                    // Skip headers that AWS SDK will manage
                    if (!shouldSkipHeader(headerName)) {
                        for (String value : headerValues) {
                            requestBuilder.appendHeader(headerName, value);
                        }
                    }
                }
            }

            // Add body if present
            if (body != null && !body.isEmpty()) {
                requestBuilder.contentStreamProvider(() ->
                        SdkBytes.fromString(body, StandardCharsets.UTF_8).asInputStream());
            }

            SdkHttpFullRequest request = requestBuilder.build();

            // Create signer parameters
            Aws4SignerParams signerParams = Aws4SignerParams.builder()
                    .awsCredentials(credentialsProvider.resolveCredentials())
                    .signingName(SERVICE_NAME)
                    .signingRegion(region)
                    .build();

            // Sign the request
            SdkHttpFullRequest signedRequest = signer.sign(request, signerParams);

            // Convert signed headers back to Spring HttpHeaders
            HttpHeaders signedHeaders = new HttpHeaders();
            signedRequest.headers().forEach((name, values) -> {
                for (String value : values) {
                    signedHeaders.add(name, value);
                }
            });

            log.debug("Successfully signed AWS IAM request");
            return signedHeaders;

        } catch (Exception e) {
            log.error("Failed to sign AWS IAM request for URL: " + url, e);
            throw new RuntimeException("Failed to sign AWS IAM request", e);
        }
    }

    /**
     * Extract AWS region from Lambda function URL
     */
    private Region extractRegionFromUrl(String url) {
        try {
            // AWS Lambda function URLs follow pattern:
            // https://{function-url-id}.lambda-url.{region}.on.aws/
            if (url.contains("lambda-url.") && url.contains(".on.aws")) {
                String[] parts = url.split("\\.");
                for (int i = 0; i < parts.length - 1; i++) {
                    if ("lambda-url".equals(parts[i])) {
                        String regionStr = parts[i + 1];
                        Region region = Region.of(regionStr);
                        log.debug("Extracted region from URL: " + regionStr);
                        return region;
                    }
                }
            }

            // Fallback: try to extract from general AWS URLs
            if (url.contains("amazonaws.com")) {
                // Pattern: https://lambda.{region}.amazonaws.com/...
                String[] parts = url.split("\\.");
                for (int i = 0; i < parts.length - 1; i++) {
                    if ("amazonaws".equals(parts[i])) {
                        String regionStr = parts[i - 1];
                        Region region = Region.of(regionStr);
                        log.debug("Extracted region from AWS URL: " + regionStr);
                        return region;
                    }
                }
            }

            // Default fallback
            log.warn("Could not extract region from URL: " + url + ", using us-east-1 as default");
            return Region.US_EAST_1;

        } catch (Exception e) {
            log.warn("Error extracting region from URL: " + url + ", using us-east-1 as default", e);
            return Region.US_EAST_1;
        }
    }

    /**
     * Convert Spring HttpMethod to AWS SDK SdkHttpMethod
     */
    private SdkHttpMethod convertToSdkHttpMethod(HttpMethod method) {
        if (method == HttpMethod.GET) {
            return SdkHttpMethod.GET;
        } else if (method == HttpMethod.POST) {
            return SdkHttpMethod.POST;
        } else if (method == HttpMethod.PUT) {
            return SdkHttpMethod.PUT;
        } else if (method == HttpMethod.DELETE) {
            return SdkHttpMethod.DELETE;
        } else if (method == HttpMethod.PATCH) {
            return SdkHttpMethod.PATCH;
        } else if (method == HttpMethod.HEAD) {
            return SdkHttpMethod.HEAD;
        } else if (method == HttpMethod.OPTIONS) {
            return SdkHttpMethod.OPTIONS;
        } else {
            log.warn("Unsupported HTTP method: " + method + ", defaulting to POST");
            return SdkHttpMethod.POST;
        }
    }

    /**
     * Determines if a header should be skipped during signing
     * (AWS SDK will manage these automatically)
     */
    private boolean shouldSkipHeader(String headerName) {
        if (headerName == null) {
            return true;
        }

        String lowerName = headerName.toLowerCase();
        return lowerName.equals("authorization") ||
                lowerName.equals("x-amz-date") ||
                lowerName.equals("x-amz-security-token") ||
                lowerName.equals("host");
    }

    /**
     * Check if AWS credentials are available
     */
    public boolean areCredentialsAvailable() {
        try {
            credentialsProvider.resolveCredentials();
            return true;
        } catch (Exception e) {
            log.warn("AWS credentials not available: " + e.getMessage());
            return false;
        }
    }
}