package com.fc.serverless.uservalidator;

import com.fc.serverless.sample.domain.UserData;
import com.fc.serverless.sample.domain.ValidationResult;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.util.function.Function;

/**
 * 🚀 Dedicated UserValidator Lambda Application
 * ONLY contains UserValidationFunction - nothing else!
 */
@SpringBootApplication
public class UserValidatorApplication {

    public static void main(String[] args) {
        System.out.println("🚀 FC UserValidator Lambda starting...");
        SpringApplication.run(UserValidatorApplication.class, args);
    }

    // Explicitly define the function bean to avoid Spring Cloud Function conflicts
    @Bean("userValidator")
    public Function<UserData, ValidationResult> userValidator() {
        return new UserValidationFunction();
    }
}