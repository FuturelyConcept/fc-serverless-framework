package com.fc.serverless.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Auto-configuration for Web MVC with sensible defaults for serverless functions
 *
 * Configures content negotiation to default to JSON responses,
 * which is what most serverless functions need.
 */
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class WebMvcAutoConfiguration implements WebMvcConfigurer {

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer
                // Default to JSON for all responses
                .defaultContentType(MediaType.APPLICATION_JSON)

                // Map 'json' media type to APPLICATION_JSON
                .mediaType("json", MediaType.APPLICATION_JSON)

                // Don't use request parameters to determine content type
                .favorParameter(false)

                // Don't use file extensions to determine content type
                .favorPathExtension(false)

                // Use Accept header for content negotiation
                .ignoreAcceptHeader(false);
    }
}