package com.fc.serverless.uservalidator;

import com.fc.serverless.sample.domain.UserData;
import com.fc.serverless.sample.domain.ValidationResult;
import org.springframework.stereotype.Component;
import java.util.function.Function;

/**
 * 🎯 Pure FDD UserValidation Function
 *
 * CRITICAL: This is the ONLY function in this Lambda!
 * When other functions try to @Autowired this, they'll get HTTP proxies!
 */
@Component("userValidator")
public class UserValidationFunction implements Function<UserData, ValidationResult> {

    @Override
    public ValidationResult apply(UserData userData) {
        System.out.println("🚀 DEDICATED UserValidator Lambda executing!");
        System.out.println("👤 Validating user: " + (userData != null ? userData.getName() : "null"));

        if (userData == null) {
            return ValidationResult.invalid("User data is null");
        }

        if (!userData.isValid()) {
            return ValidationResult.invalid("User data validation failed");
        }

        System.out.println("✅ User validation passed for: " + userData.getName());
        return ValidationResult.valid();
    }
}