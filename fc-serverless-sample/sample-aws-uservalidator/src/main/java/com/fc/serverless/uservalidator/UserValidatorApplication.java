// fdd-lambda-uservalidator/src/main/java/com/fdd/lambda/uservalidator/UserValidatorApplication.java
package com.fdd.lambda.uservalidator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.util.function.Supplier;
/**
 * 🚀 Dedicated UserValidator Lambda Application
 * ONLY contains UserValidationFunction - nothing else!
 */
@SpringBootApplication
public class UserValidatorApplication {
    public static void main(String[] args) {
        System.out.println("🚀 FDD UserValidator Lambda starting...");
        SpringApplication.run(UserValidatorApplication.class, args);
    }

    @Bean("sup")
    public Supplier<String> sup() {
        return () -> "Whats up";
    }
}